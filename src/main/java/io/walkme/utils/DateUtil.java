package utils;

public class DateUtil {

    /**
     * @return количество секунд, прошедших с 00:00
     */
    public static long fromHHMMToLong(String hhmm) {
        String[] tokens = hhmm.split(":");

        long h = Integer.parseInt(tokens[0]);
        long m = Integer.parseInt(tokens[1]);

        return h * 3600 + m * 60;
    }

    public static String fromLongToHHMM(long hhmm) {
        long h = hhmm / 3600;
        long m = (hhmm - (h * 3600)) / 60;

        String th, tm;

        if (h / 10 == 0) {
            th = "0" + h;
        } else {
            th = String.valueOf(h);
        }

        if (m / 10 == 0) {
            tm = "0" + m;
        } else {
            tm = String.valueOf(m);
        }

        return th + ":" + tm;
    }
}
