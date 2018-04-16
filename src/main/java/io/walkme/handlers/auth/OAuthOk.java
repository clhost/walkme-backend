package io.walkme.handlers.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.helpers.OKHelper;
import io.netty.channel.ChannelHandlerContext;
import io.walkme.response.ResultBuilder;
import io.walkme.services.fields.UserFields;
import io.walkme.storage.entities.Session;
import io.walkme.utils.MD5Encoder;
import io.walkme.utils.ResponseBuilder;
import io.walkme.utils.SHA256HEXEncoder;
import org.apache.http.client.fluent.Request;
import io.walkme.services.EntityService;
import io.walkme.services.SessionService;
import io.walkme.services.UserService;
import io.walkme.storage.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

class OAuthOk {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "uid";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHOTO = "pic190x190";

    private final SHA256HEXEncoder tokenEncoder = new SHA256HEXEncoder();
    private final MD5Encoder md5Encoder = new MD5Encoder();
    private static final SessionService sessionService = SessionService.getInstance();
    private static final EntityService<User, String> userService = new UserService();

    private String sessionSecretKey;
    private String sig;
    private String code;
    private String salt;
    private String sessionToken;
    private String accessToken;
    private String userId;
    private String firstName;
    private String lastName;
    private String photo;

    private Request apacheRequest;
    private InputStream stream;
    private BufferedReader reader;
    private StringBuilder respBuilder;

    private ChannelHandlerContext ctx;
    private Logger logger = LogManager.getLogger(OAuthOk.class);
    private static final JsonParser jsonParser = new JsonParser();

    void handle(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        this.ctx = ctx;

        // get access token --------------------------------------------------------------------------------------------
        code = params.get("code").get(0);

        apacheRequest = Request.Post(OKHelper.accessTokenString(code));
        stream = apacheRequest.execute().returnResponse().getEntity().getContent();

        reader = new BufferedReader(new InputStreamReader(stream));
        respBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            respBuilder.append(line).append("\n");
        }

        JsonObject jsonObject = jsonParser.parse(respBuilder.toString()).getAsJsonObject();

        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals(ACCESS_TOKEN)) {
                accessToken = element.getValue().getAsString();
            }
        }

        // generate salt and session token -----------------------------------------------------------------------------
        salt = SHA256HEXEncoder.salt();
        sessionToken = tokenEncoder.encode(salt + System.currentTimeMillis());

        profileInfo();
    }

    private void profileInfo() throws Exception {
        if (accessToken == null) {
            logger.warn("Can't authorize. Access token is null. Cause: invalid code " + code + ".");
            ctx.writeAndFlush(ResultBuilder.asJson(403, "can't authorize.", ResultBuilder.ResultType.ERROR));
            ctx.close();
            return;
        }

        sessionSecretKey = md5Encoder.encode(accessToken + OKHelper.clientSecret());
        sig = md5Encoder.encode("application_key=" + OKHelper.clientPublic() +
                "fields=" + FIRST_NAME + "," + LAST_NAME + "," + PHOTO +
                "method=users.getCurrentUser" + sessionSecretKey).toLowerCase();

        apacheRequest = Request.Get(OKHelper.userProfileInfoString(accessToken, sig));
        stream = apacheRequest.execute().returnResponse().getEntity().getContent();

        reader = new BufferedReader(new InputStreamReader(stream));
        respBuilder.setLength(0);

        String line;
        while ((line = reader.readLine()) != null) {
            respBuilder.append(line);
        }

        JsonObject jsonObject = jsonParser.parse(respBuilder.toString()).getAsJsonObject();

        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals(FIRST_NAME)) {
                firstName = element.getValue().getAsString();
            }

            if (element.getKey().equals(LAST_NAME)) {
                lastName = element.getValue().getAsString();
            }

            if (element.getKey().equals(USER_ID)) {
                userId = element.getValue().getAsString();
            }

            if (element.getKey().equals(PHOTO)) {
                photo = element.getValue().getAsString();
            }
        }


        if (firstName == null || accessToken == null) {
            logger.warn("Can't get profile info.");
            ctx.writeAndFlush(ResultBuilder.asJson(403, "can't authorize.", ResultBuilder.ResultType.ERROR));
            ctx.close();
            return;
        }

        saveUser();
    }

    private void saveUser() throws Exception {
        User user = new User();
        user.setSalt(salt);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSocialId(Long.parseLong(userId));
        user.setAvatar(photo);

        User exUser = userService.get(String.valueOf(user.getSocialId()), UserFields.SOCIAL_ID);
        if (exUser == null) {
            userService.save(user);
            sessionService.saveSession(new Session(user, sessionToken, accessToken, "ok"));
        } else {
            // обновить, если имя, фамилия или аватар изменены
            if (!user.equals(exUser)) {
                exUser.setAvatar(user.getAvatar());
                exUser.setFirstName(user.getFirstName());
                exUser.setLastName(user.getLastName());
                userService.update(exUser);
            }

            sessionToken = tokenEncoder.encode(exUser.getSalt() + System.currentTimeMillis());
            sessionService.saveSession(new Session(exUser, sessionToken, accessToken, "ok"));
        }

        JsonObject object = new JsonObject();
        object.addProperty("token", sessionToken);

        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.OK,
                ResultBuilder.asJson(
                        200, object,
                        ResultBuilder.ResultType.RESULT)));
        ctx.close();
    }
}