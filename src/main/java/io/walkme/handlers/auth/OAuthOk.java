package io.walkme.handlers.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.walkme.helpers.OKHelper;
import io.netty.channel.ChannelHandlerContext;
import org.apache.http.client.fluent.Request;
import io.walkme.services.GenericEntityService;
import io.walkme.services.SessionService;
import io.walkme.services.UserService;
import io.walkme.storage.entities.User;
import io.walkme.utils.SHA256BASE64Encoder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

class OAuthOk {
    private static final SHA256BASE64Encoder tokenEncoder = new SHA256BASE64Encoder();
    private static final SessionService sessionService = SessionService.getInstance();
    private static final GenericEntityService<User, String> userService = new UserService();

    static void handle(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        String code = params.get("code").get(0);

        Request apacheRequest = Request.Get(OKHelper.accessTokenString(code));
        InputStream stream = apacheRequest.execute().returnResponse().getEntity().getContent();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();
        String accessToken = null, tokenType = null, refreshToken = null, expiresIn = null;

        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals("access_token")) {
                accessToken = element.getValue().getAsString();
            }

            if (element.getKey().equals("token_type")) {
                tokenType = element.getValue().getAsString();
            }

            if (element.getKey().equals("refresh_token")) {
                refreshToken = element.getValue().getAsString();
            }

            if (element.getKey().equals("expires_in")) {
                expiresIn = element.getValue().getAsString();
            }

        }

        // for today logging
        System.out.println("(" + accessToken + ", " + tokenType + ", " + refreshToken + ", " + expiresIn + ")");
        // put to db
    }
}