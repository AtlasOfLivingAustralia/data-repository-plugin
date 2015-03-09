package au.org.ala.util

import java.sql.Timestamp
import java.text.ParsePosition
import java.text.SimpleDateFormat

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class ISO8601HelperTest extends GroovyTestCase {
    static def TS1 = 1422966896789
    static def TS2 = 1422966896000
    static def TZO1 = - 5 * 3600 * 1000

    void testParseSDF1() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        format.setLenient(false)
        ParsePosition pos = new ParsePosition(0)
        String input = "2015-02-03T12:34:56.789+05:00"
        Date date = format.parse(input, pos)

        assertNotNull(date)
        assertEquals(input.length(), pos.index)
        assertEquals(TS1 + TZO1, date.time)
    }

    void testParseSDF2() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        format.setLenient(false)
        ParsePosition pos = new ParsePosition(0)
        String input = "2015-02-03T12:34:56.7+05:00"
        Date date = format.parse(input, pos)

        assertNotNull(date)
        assertEquals(input.length(), pos.index)
        assertEquals(TS1 + TZO1 - 789 + 7, date.time)
    }

    void testParseSDF3() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ")
        format.setLenient(false)
        ParsePosition pos = new ParsePosition(0)
        String input = "2015-02-03T12:34:56.789+0500"
        Date date = format.parse(input, pos)

        assertNotNull(date)
        assertEquals(input.length(), pos.index)
        assertEquals(TS1 + TZO1, date.time)
    }

    void testParseTimestamp1() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56.789+05:00");
        assertEquals(TS1 + TZO1, timestamp.time)
    }

    void testParseTimestamp2() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56.789+0500");
        assertEquals(TS1 + TZO1, timestamp.time)
    }

    // Note doesn't handle just hours nicely
    void testParseTimestamp3() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56.789+05");
        assertEquals(TS1 - TimeZone.default.getOffset(timestamp.time), timestamp.time)
    }

    void testParseTimestamp4() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56.789Z");
        assertEquals(TS1, timestamp.time)
    }

    void testParseTimestamp5() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56.789");
        assertEquals(TS1 - TimeZone.default.getOffset(timestamp.time), timestamp.time)
    }

    void testParseTimestamp8() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56+05:00");
        assertEquals(TS2 + TZO1, timestamp.time)
    }

    void testParseTimestamp9() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56+0500");
        assertEquals(TS2 + TZO1, timestamp.time)
    }

    // Note doesn't handle just hours nicely
    void testParseTimestamp10() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56+05");
        assertEquals(TS2 - TimeZone.default.getOffset(timestamp.time), timestamp.time)
    }

    void testParseTimestamp11() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56Z");
        assertEquals(TS2, timestamp.time)
    }

    void testParseTimestamp12() {
        Timestamp timestamp = ISO8601Helper.parseTimestamp("2015-02-03T12:34:56");
        assertEquals(TS2 - TimeZone.default.getOffset(timestamp.time), timestamp.time)
    }
}
