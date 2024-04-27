package com.leedanieluk.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class DateUtil {
    public static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ofPattern("MMM-uuuu"))
                .toFormatter(Locale.ENGLISH);
        YearMonth yearMonth = YearMonth.parse(date, formatter);
        return yearMonth.atEndOfMonth();
    }}
