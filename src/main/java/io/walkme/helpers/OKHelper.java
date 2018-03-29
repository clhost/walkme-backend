package io.walkme.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OKHelper {
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
                "client_id=" + CLIENT_ID + "&" +
                "scope=" + SCOPE + "&" +
                "response_type=code" + "&" +
                "redirect_uri=" + REDIRECT_URI + "&" +
                "state=" + STATE;
    }

    public static String accessTokenString(String code) {
        return API_URL +
                "code=" + code + "&" +
                "client_id=" + CLIENT_ID + "&" +
                "client_secret=" + CLIENT_SECRET + "&" +
                "redirect_uri=" + REDIRECT_URI + "&" +
                "grant_type=authorization_code";
    }
}
