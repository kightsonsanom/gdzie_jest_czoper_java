package com.example.asinit_user.gdziejestczoper.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Converters {

    public static String longToString(long date) {
        //dodanie godziny, bo czas ziomowy
        Date data = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", new Locale("pl"));

//        TimeZone tz = TimeZone.getDefault();
//        Calendar cal = GregorianCalendar.getInstance(tz);
//        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
//
//        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
//        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
//        String parseString = sdf.format(data) + offset;

        return sdf.format(data);
    }

}
