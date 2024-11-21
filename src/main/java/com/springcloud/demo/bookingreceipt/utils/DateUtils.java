package com.springcloud.demo.bookingreceipt.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(OffsetDateTime date){
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
        String dateNum = String.format("%2d", date.getDayOfMonth()).replace(' ', '0');
        int year = date.getYear();
        String hours = String.format("%2d", date.getHour()).replace(' ', '0');
        String minutes = String.format("%2d", date.getMinute()).replace(' ', '0');

        return StringUtils.capitalize(dayName) + " " + dateNum + " de " + StringUtils.capitalize(monthName) + " de " + year + " a las " + hours + ":" + minutes + " hs";
    }
}
