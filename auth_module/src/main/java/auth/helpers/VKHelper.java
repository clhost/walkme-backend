package auth.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VKHelper extends SocialHelper {
    private static String OAUTH_URL;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String REDIRECT_URI;
    private static String SCOPE;
    private static String API_VERSION;
    private static final String PHOTO = "photo_200";
    private static final String STATE = "vk";

    public static void init() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(ConfigHelper.LOCAL_PROPERTIES)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OAUTH_URL = properties.getProperty("vk.oauth_url");
        API_URL = properties.getProperty("vk.api_url");
        CLIENT_ID = properties.getProperty("vk.client_id");
        CLIENT_SECRET = properties.getProperty("vk.client_secret");
        REDIRECT_URI = properties.getProperty("vk.redirect_uri");
        SCOPE = properties.getProperty("vk.scope");
        API_VERSION = properties.getProperty("vk.api_version");
    }

    public static String authString() {
        return OAUTH_URL + "authorize?" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "scope=" + encodeURIComponent(SCOPE) + "&" +
                "response_type=code" + "&" +
                "v=" + encodeURIComponent(API_VERSION) + "&" +
                "state=" + encodeURIComponent(STATE);

    }

    public static String accessTokenString(String code) {
        return OAUTH_URL + "access_token?" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "client_secret=" +  encodeURIComponent(CLIENT_SECRET) + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "code=" + encodeURIComponent(code);
    }

    public static String userProfileInfoString(String accessToken, String userId) {
        return API_URL + "users.get?user_ids=" + encodeURIComponent(userId) + "&" +
                "fields=" + PHOTO + "&" +
                "access_token=" + encodeURIComponent(accessToken) + "&" +
                "v=" + encodeURIComponent(API_VERSION);
    }

    public static String friendListString(String accessToken, String userId) {
        return API_URL + "friends.get?user_id=" + encodeURIComponent(userId) + "&" +
                "order=random" + "&" +
                "v=" + encodeURIComponent(API_VERSION);
    }

    public static void main(String[] args) {
        VKHelper.init();
        System.out.println(VKHelper.authString());
    }
}
