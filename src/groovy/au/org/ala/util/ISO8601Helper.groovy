package au.org.ala.util

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class ISO8601Helper {
    /** Formats to try for timestamps */
    static final def TIMESTAMP_FORMATS = [
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSzzz"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
    ]

    synchronized static Timestamp parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty())
            return null
        for (SimpleDateFormat format: TIMESTAMP_FORMATS) {
            try {
                return new Timestamp(format.parse(timestamp).getTime())
            } catch (ParseException ex) {
            }
        }
        throw new ParseException("Unable to parse timestamp " + timestamp, 0)
    }
}
