package io.walkme.handlers.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.walkme.helpers.VKHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.http.client.fluent.Request;
import io.walkme.services.GenericEntityService;
import io.walkme.services.SessionService;
import io.walkme.services.UserService;
import io.walkme.services.fields.UserFields;
import io.walkme.storage.entities.Session;
import io.walkme.storage.entities.User;
import io.walkme.utils.ResponseBuilder;
import io.walkme.utils.SHA256BASE64Encoder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class OAuthVk {
    private static final SHA256BASE64Encoder tokenEncoder = new SHA256BASE64Encoder();
    private static final SessionService sessionService = SessionService.getInstance();
    private static final GenericEntityService<User, String> userService = new UserService();


    static void handle(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        String code = params.get("code").get(0);

        Request apacheRequest = Request.Get(VKHelper.accessTokenString(code));
        InputStream stream = apacheRequest.execute().returnResponse().getEntity().getContent();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();
        Optional<String> accessToken = Optional.empty(), userId = Optional.empty();
        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals("access_token")) {
                accessToken = Optional.of(element.getValue().getAsString());
            }

            if (element.getKey().equals("user_id")) {
                userId = Optional.of(element.getValue().getAsString());
            }
        }

        String salt = SHA256BASE64Encoder.salt();
        String sessionToken = tokenEncoder.encode(salt + System.currentTimeMillis());


        // get profile info
        if (accessToken.isPresent() && userId.isPresent()) {
            apacheRequest = Request.Get(VKHelper.userProfileInfoString(accessToken.get(), userId.get()));
        }

        stream = apacheRequest.execute().returnResponse().getEntity().getContent();

        reader = new BufferedReader(new InputStreamReader(stream));
        builder.setLength(0);

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        System.out.println(builder.toString());

        jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();
        Optional<String> firstName = Optional.empty(), lastName = Optional.empty();
        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals("response")) {
                JsonObject object = element.getValue().getAsJsonArray().get(0).getAsJsonObject();
                firstName = Optional.of(object.get("first_name").getAsString());
                lastName = Optional.of(object.get("last_name").getAsString());
            }
        }

        if (!userId.isPresent() || !accessToken.isPresent()) {
            throw new Exception("User id or access token is empty.");
        }

        User user = new User();
        user.setSalt(salt);
        user.setFirstName(firstName.orElse("default"));
        user.setLastName(lastName.orElse("default"));
        user.setSocialId(Long.parseLong(userId.get()));
        user.setAvatar("empty");

        User exUser = userService.get(String.valueOf(user.getSocialId()), UserFields.SOCIAL_ID);
        if (exUser == null) {
            userService.save(user);
            sessionService.saveSession(new Session(user, sessionToken, accessToken.get(), "vk"));
        } else {
            sessionToken = tokenEncoder.encode(exUser.getSalt() + System.currentTimeMillis());
            sessionService.saveSession(new Session(exUser, sessionToken, accessToken.get(), "vk"));
        }

        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.OK,
                "{\n" + "    \"token\": " + sessionToken + "\n" + "}"));
        ctx.close();
    }
}
