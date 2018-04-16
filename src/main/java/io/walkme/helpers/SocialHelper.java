package io.walkme.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

abstract class SocialHelper {
    static String encodeURIComponent(String s) {
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
}
