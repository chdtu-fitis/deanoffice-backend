package ua.edu.chdtu.deanoffice.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    public static String getYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static int getYearInt(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static String getMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return makeFormat(calendar.get(Calendar.MONTH) + 1);
    }

    public static String getDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return makeFormat(calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static String makeFormat(int number) {
        return number >= 10 ? String.valueOf(number) : "0" + number;
    }

    public static String getDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.format("%s.%s.%d", makeFormat(calendar.get(Calendar.DAY_OF_MONTH)),
                makeFormat(calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.YEAR));
    }

    public static Date setDateHours(Date date, int hours, int minutes) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        date = calendar.getTime();
        return date;
    }
}
