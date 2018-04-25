package auth.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OKHelper extends SocialHelper {
    private static String OAUTH_URL;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String CLIENT_PUBLIC;
    private static String SCOPE;
    private static String REDIRECT_URI;
    private static final String STATE = "ok";

    private static final String PHOTO = "pic190x190";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";


    public static void init() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(new File(ConfigHelper.LOCAL_PROPERTIES)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        OAUTH_URL = properties.getProperty("ok.oauth_url");
        API_URL = properties.getProperty("ok.api_url");
        CLIENT_ID = properties.getProperty("ok.client_id");
        CLIENT_SECRET = properties.getProperty("ok.client_secret");
        CLIENT_PUBLIC = properties.getProperty("ok.client_public");
        REDIRECT_URI = properties.getProperty("ok.redirect_uri");
        SCOPE = properties.getProperty("ok.scope");
    }

    public static String authString() {
        return OAUTH_URL + "authorize?" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "scope=" + encodeURIComponent(SCOPE) + "&" +
                "response_type=code" + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "state=" + encodeURIComponent(STATE);
    }

    public static String accessTokenString(String code) {
        return API_URL +
                "code=" + encodeURIComponent(code) + "&" +
                "client_id=" + encodeURIComponent(CLIENT_ID) + "&" +
                "client_secret=" + encodeURIComponent(CLIENT_SECRET) + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "grant_type=authorization_code";
    }

    public static String userProfileInfoString(String accessToken, String sig) {
        return "https://api.ok.ru/fb.do?application_key=" + encodeURIComponent(CLIENT_PUBLIC) +
                "&fields=" + FIRST_NAME + "," + LAST_NAME + "," + PHOTO +
                "&method=users.getCurrentUser&sig=" + encodeURIComponent(sig) +
                "&access_token=" + encodeURIComponent(accessToken);
    }

    public static String clientSecret() {
        return CLIENT_SECRET;
    }

    public static String clientPublic() {
        return CLIENT_PUBLIC;
    }
}
