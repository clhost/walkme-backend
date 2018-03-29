package io.walkme.helpers;

import io.walkme.utils.HibernateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

public class VKHelper {
    private static String OAUTH_URL;
    private static String API_URL;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String REDIRECT_URI;
    private static String SCOPE;
    private static String API_VERSION;
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
                "client_id=" + CLIENT_ID + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "scope=" + SCOPE + "&" +
                "response_type=code" + "&" +
                "v=" + API_VERSION + "&" +
                "state=" + STATE;

    }

    public static String accessTokenString(String code) {
        return OAUTH_URL + "access_token?" +
                "client_id=" + CLIENT_ID + "&" +
                "client_secret=" +  CLIENT_SECRET + "&" +
                "redirect_uri=" + encodeURIComponent(REDIRECT_URI) + "&" +
                "code=" + code;
    }

    public static String userProfileInfoString(String accessToken, String userId) {
        return API_URL + "users.get?user_ids=" + userId + "&" +
                "access_token=" + accessToken + "&" +
                "v=" + API_VERSION;
    }

    private static String encodeURIComponent(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e){
            result = s;
        }

        return result;
    }

    public static void main(String[] args) {
        VKHelper.init();
        System.out.println(VKHelper.authString());
    }
}
