package auth.oauth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import auth.entities.Session;
import auth.entities.User;
import auth.entities.UserFields;
import auth.helpers.OKHelper;
import auth.utils.SHA256HEXEncoder;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class OAuthOkAuthorizer extends AbstractOAuthAuthorizer {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "uid";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHOTO = "pic190x190";
    private static final Logger logger = LogManager.getLogger(OAuthOkAuthorizer.class);

    private String sessionSecretKey;
    private String sig;
    private String salt;
    private String sessionToken;
    private String accessToken;
    private String userId;
    private String firstName;
    private String lastName;
    private String photo;

    public OAuthOkAuthorizer(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    @Nullable
    @Override
    public String authorize(String code) {
        JsonObject jsonObject = oAuthRequest(OKHelper.accessTokenString(code), RequestType.POST);
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
        sessionSecretKey = md5Encoder.encode(accessToken + OKHelper.clientSecret());
        sig = md5Encoder.encode(
                "application_key=" + OKHelper.clientPublic() +
                "fields=" + FIRST_NAME + "," + LAST_NAME + "," + PHOTO +
                "method=users.getCurrentUser" + sessionSecretKey)
                .toLowerCase();

        JsonObject jsonObject = oAuthRequest(OKHelper.userProfileInfoString(accessToken, sig), RequestType.POST);
        if (jsonObject.get("error") != null) {
            logger.error("Can't get profile info.");
            return null;
        }

        try {
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
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
        return saveUserAndReturnToken();
    }

    private String saveUserAndReturnToken() {
        User user = new User();
        user.setSalt(salt);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSocialId(Long.parseLong(userId));
        user.setAvatar(photo);

        try {
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
            return sessionToken;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}