package com.artisans.qwikhomeservices.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GetDateTime {
    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);

        switch (day % 10) {
            case 1:
                return new SimpleDateFormat("EEE MMMM d'st', yyyy ' @ ' hh:mm aa", Locale.getDefault()).format(date);
            case 2:
                return new SimpleDateFormat("EEE MMMM d'nd', yyyy ' @ ' hh:mm aa", Locale.getDefault()).format(date);
            case 3:
                return new SimpleDateFormat("EEE MMMM d'rd', yyyy ' @ ' hh:mm aa", Locale.getDefault()).format(date);
            default:
                return new SimpleDateFormat("EEE MMMM d'th', yyyy ' @ ' hh:mm aa", Locale.getDefault()).format(date);
        }

    }

    public static String DateToTimeFormat(String oldStringDate) {
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldStringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    public static String DateFormat(String oldStringDate) {
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy",
                new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .parse(oldStringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldStringDate;
        }

        return newDate;
    }

    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String country = locale.getLanguage();
        return country.toLowerCase();
    }

}
