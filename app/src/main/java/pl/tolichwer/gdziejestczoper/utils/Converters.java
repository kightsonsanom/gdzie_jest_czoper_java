package pl.tolichwer.gdziejestczoper.utils;

import android.os.Environment;

import pl.tolichwer.gdziejestczoper.viewobjects.Position;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

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

    public static void sortPositions(List<Position> data) {
        Collections.sort(data, (o1, o2) -> ((int) (o1.getFirstLocationDate() - o2.getFirstLocationDate())));
    }

    public static void appendLog(String text) {
        String fileName = "czoperlog.txt";
        File logFile = new File(Environment.getExternalStorageDirectory(), fileName);
        String date = getCurrentDay() + " " + longToString(System.currentTimeMillis()) + "  ";

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(date);
            buf.append(text);
            buf.append("\r\n");
            buf.newLine();
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromLogFile() {

        String fileName = "czoperlog.txt";
        File logFile = new File(Environment.getExternalStorageDirectory(), fileName);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(logFile));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            Timber.d("Content of the file: " + everything);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
