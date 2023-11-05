package com.fm.base.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class DateTimeUtils {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_PATTERN_SLASH = "yyyy/MM/dd";
    private static final ClientErrorException INVALID_DATE = new ClientErrorException("date format yyyy-MM-dd", Response.Status.BAD_REQUEST);

    public static DateTime parseDateFromString(String tsString, DateTimeZone timezone) {
        DateTimeFormatter dateTimeFormatterDash = DateTimeFormat.forPattern(DATE_PATTERN);
        DateTime ts;
        try {
            ts = dateTimeFormatterDash.withZone(timezone).parseDateTime(tsString);
        } catch (Exception dashError) {
            DateTimeFormatter dateTimeFormatterSlash = DateTimeFormat.forPattern(DATE_PATTERN_SLASH);
            try {
                ts = dateTimeFormatterSlash.withZone(timezone).parseDateTime(tsString);
            } catch (Exception slashError) {
                throw INVALID_DATE;
            }
        }
        return ts;
    }

    public static DateTime parseDateFromString(String tsString, DateTimeZone timezone, boolean endDate) {
        DateTime ts = parseDateFromString(tsString, timezone);
        if (endDate) {
            ts = ts.plusDays(1);
        }
        return ts;
    }
}
