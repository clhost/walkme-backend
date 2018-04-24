package auth.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    private MessageDigest digest;

    public MD5Encoder() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("In MD5Encoder constructor:" + e.getMessage());
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

            return Hex.encodeHexString(pass);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
