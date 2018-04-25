package auth.oauth;

import auth.entities.Session;
import auth.entities.User;
import auth.entities.UserFields;
import auth.helpers.FBHelper;
import auth.utils.SHA256HEXEncoder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class OAuthFbAuthorizer extends AbstractOAuthAuthorizer {
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "id";
    private static final String PICTURE = "picture";
    private static final String PICTURE_DATA = "data";
    private static final String PICTURE_URL = "url";
    private static final Logger logger = LogManager.getLogger(OAuthFbAuthorizer.class);

    private String salt;
    private String sessionToken;
    private String accessToken;
    private String userId;
    private String firstName;
    private String lastName;
    private String photo;

    public OAuthFbAuthorizer(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    @Nullable
    @Override
    public String authorize(String code) {
        JsonObject jsonObject = oAuthRequest(FBHelper.accessTokenString(code), RequestType.GET);
        if (jsonObject.get("error") != null) {
            logger.error("Can't authorize. Access token is null. Cause: invalid code " + code + ".");
            return null;
        }

        try {
            for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
                if (element.getKey().equals(ACCESS_TOKEN)) {
                    accessToken = element.getValue().getAsString();
                }
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }

        salt = SHA256HEXEncoder.salt();
        sessionToken = tokenEncoder.encode(salt + System.currentTimeMillis());
        return profileInfo();
    }

    private String profileInfo() {
        JsonObject jsonObject = oAuthRequest(FBHelper.getUserIdAndNameString(accessToken), RequestType.GET);
        if (jsonObject.get("error") != null) {
            logger.error("Can't get profile info.");
            return null;
        }

        try {
            userId = jsonObject.get(USER_ID).getAsString();
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return null;
        }

        jsonObject = oAuthRequest(FBHelper.userProfileInfoString(accessToken, userId), RequestType.GET);
        if (jsonObject.get("error") != null) {
            logger.error("Can't get profile info.");
            return null;
        }

        try {
            firstName = jsonObject.get(FIRST_NAME).getAsString();
            lastName = jsonObject.get(LAST_NAME).getAsString();
            photo = jsonObject
                    .get(PICTURE).getAsJsonObject()
                    .get(PICTURE_DATA).getAsJsonObject()
                    .get(PICTURE_URL).getAsString();
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
            return null;
        }
        return saveUserAndReturnToken();
    }

    private String saveUserAndReturnToken() {
        User user = new User();
        user.setSalt(salt);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSocialId(Long.parseLong(userId));
        user.setSocialNetwork("fb");
        user.setAvatar(photo);

        try {
            User exUser = userService.get(String.valueOf(user.getSocialId()), UserFields.SOCIAL_ID);
            if (exUser == null) {
                userService.save(user);
                sessionService.saveSession(new Session(user, sessionToken, accessToken, "fb"));
            } else {
                // обновить, если имя, фамилия или аватар изменены
                if (!user.equals(exUser)) {
                    exUser.setAvatar(user.getAvatar());
                    exUser.setFirstName(user.getFirstName());
                    exUser.setLastName(user.getLastName());
                    userService.update(exUser);
                }
                sessionToken = tokenEncoder.encode(exUser.getSalt() + System.currentTimeMillis());
                sessionService.saveSession(new Session(exUser, sessionToken, accessToken, "fb"));
            }
            return sessionToken;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
