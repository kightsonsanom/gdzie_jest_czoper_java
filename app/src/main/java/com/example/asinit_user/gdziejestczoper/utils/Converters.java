package com.example.asinit_user.gdziejestczoper.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Converters {

    public static String longToString(long date) {
        Date data = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("pl"));

        return sdf.format(data);
    }

    public static String setSerializedDateString(String date) {
        Date data = null;
        try {
            data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("pl"));

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        return sdf.format(data) + offset;

    }

    public static String getCurrentDay() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM", new Locale("pl"));
        return format.format(date);
    }

    public static String getDayFromMilis(long i) {
        Date date = new Date(i);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM", new Locale("pl"));
        return format.format(date);
    }
}
