package com.juan.frameanimdemo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangxin on 16/5/10.
 */
public class DateUtil {

    public static String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static boolean isIn(String startDate, String endDate) {
        long startDate1 = parseDatetimeToTime(startDate);
        long endDate1 = parseDatetimeToTime(endDate);

        long current = System.currentTimeMillis();
        if (current > startDate1 && current < endDate1) {
            return true;
        }

        return false;
    }


    public static long parseDatetimeToTime(String datetime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            Date d = sdf.parse(datetime);
            return d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
