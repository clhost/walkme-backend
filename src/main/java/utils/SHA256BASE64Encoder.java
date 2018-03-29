package utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Base64;

public class SHA256BASE64Encoder {
    private MessageDigest digest;

    public SHA256BASE64Encoder() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("In SHA256BASE64Encoder constructor:" + e.getMessage());
        }
    }

    public String encode(String str) {
        if (str == null) {
            return null;
        }

        try {
            byte[] pass;
            byte[] bytes = str.getBytes("UTF-8");

            pass = digest.digest(bytes);
            return Base64.getEncoder().encodeToString(pass);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String salt() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        int n = random.nextInt(10) + 10;

        for (int i = 0; i < n; i++) {
            builder.append(Character.toString((char) (random.nextInt(126 - 33) + 33)));
        }

        return builder.toString();
    }
}
