package com.amaizzzing.amaizingweather.functions;

import java.util.Calendar;
import java.util.Date;

public class DateFunctions {
    public static long atEndOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime().getTime();
    }

    public static long atStartOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }
}
