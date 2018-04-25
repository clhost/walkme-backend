package auth.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FBHelper extends SocialHelper {
    private static String OAUTH_URL;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String REDIRECT_URI;
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PICTURE = "picture.type(large)";
    private static final String STATE = "fb";

    public static void init() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(ConfigHelper.LOCAL_PROPERTIES)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OAUTH_URL = properties.getProperty("fb.oauth_url");
        API_URL = properties.getProperty("fb.graph_api_url");
        CLIENT_ID = properties.getProperty("fb.client_id");
        REDIRECT_URI = properties.getProperty("fb.redirect_uri");
        CLIENT_SECRET = properties.getProperty("fb.client_secret");
    }

    public static String authString() {
        return OAUTH_URL + "oauth?" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "state=" + encodeURIComponent(STATE);
    }

    public static String accessTokenString(String code) {
        return API_URL + "oauth/access_token?" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "client_secret=" +  encodeURIComponent(CLIENT_SECRET) + "&" +
                "code=" + encodeURIComponent(code);
    }

    public static String getUserIdAndNameString(String accessToken) {
        return API_URL + "me?access_token=" + encodeURIComponent(accessToken);
    }

    public static String userProfileInfoString(String accessToken, String userId) {
        return API_URL + encodeURIComponent(userId) + "/" +
                "?fields=" + encodeURIComponent(FIRST_NAME) + "," +
                encodeURIComponent(LAST_NAME) + "," +
                encodeURIComponent(PICTURE) + "&" +
                "access_token=" + encodeURIComponent(accessToken);
    }
}
