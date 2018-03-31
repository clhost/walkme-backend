package io.walkme.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

public class OKHelper extends SocialHelper {
    private static String OAUTH_URL;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String SCOPE;
    private static String REDIRECT_URI;
    private static final String STATE = "ok";

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
}
