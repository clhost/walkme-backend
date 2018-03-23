package utils;

public class DateUtil {

    /**
     * @return количество секунд, прошедших с 00:00
     */
    public static long fromHHMMToLong(String hhmm) {
        String[] tokens = hhmm.split(":");

        int h = Integer.parseInt(tokens[0]);
        int m = Integer.parseInt(tokens[1]);

        return h * 3600 + m * 60;
    }
}
