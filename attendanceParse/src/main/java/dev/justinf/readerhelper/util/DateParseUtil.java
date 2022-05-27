package dev.justinf.readerhelper.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class DateParseUtil {

    // Google Sheets format: M/D/yyyy H:mm:ss
    private static final DateTimeFormatter dtf;

    static {
        dtf = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss").withZone(ZoneId.systemDefault());
    }

    public static Instant fromTimestamp(String timestamp) throws DateTimeParseException {
        TemporalAccessor ta = dtf.parse(timestamp);
        return Instant.from(ta);
    }
}