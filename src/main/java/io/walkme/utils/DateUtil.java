package io.walkme.utils;

public class DateUtil {

    /**
     *
     * @param hhmm часы и минуты в формате hh:mm
     * @return количество секунд, прошедших с 00:00
     */
    public static long fromHHMMToLong(String hhmm) {
        String[] tokens = hhmm.split(":");

        long hours = Integer.parseInt(tokens[0]);
        long minutes = Integer.parseInt(tokens[1]);

        return hours * 3600 + minutes * 60;
    }

    /**
     *
     * @param hhmm количество секунд, прошедших с 00:00
     * @return строковое представление hhmm в формате hh:mm
     */
    public static String fromLongToHHMM(long hhmm) {
        long hours = hhmm / 3600;
        long minutes = (hhmm - (hours * 3600)) / 60;

        String hh;
        String mm;

        if (hours / 10 == 0) {
            hh = "0" + hours;
        } else {
            hh = String.valueOf(hours);
        }

        if (minutes / 10 == 0) {
            mm = "0" + minutes;
        } else {
            mm = String.valueOf(minutes);
        }

        return hh + ":" + mm;
    }
}
