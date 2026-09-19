package org.filteredpush.qc.date.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for NumberUtils class to ensure proper handling of decimal values
 * that represent integers (e.g., "29.0" -> 29).
 */
public class NumberUtilsTest {

    @Test
    public void testParseIntWithDecimalValues() {
        // Test decimal values that equal integers
        assertEquals(29, NumberUtils.parseInt("29.0"));
        assertEquals(12, NumberUtils.parseInt("12.0"));
        assertEquals(10, NumberUtils.parseInt("10.0"));
        assertEquals(20, NumberUtils.parseInt("20.0"));
        assertEquals(5, NumberUtils.parseInt("5.0"));
        assertEquals(9, NumberUtils.parseInt("9.0"));
        assertEquals(15, NumberUtils.parseInt("15.0"));
        assertEquals(30, NumberUtils.parseInt("30.0"));
        assertEquals(13, NumberUtils.parseInt("13.0"));
        assertEquals(7, NumberUtils.parseInt("7.0"));
        assertEquals(31, NumberUtils.parseInt("31.0"));
        assertEquals(1, NumberUtils.parseInt("1.0"));
        assertEquals(19, NumberUtils.parseInt("19.0"));
        assertEquals(2, NumberUtils.parseInt("2.0"));
        assertEquals(27, NumberUtils.parseInt("27.0"));
        assertEquals(24, NumberUtils.parseInt("24.0"));
        assertEquals(4, NumberUtils.parseInt("4.0"));
        assertEquals(3, NumberUtils.parseInt("3.0"));
        assertEquals(8, NumberUtils.parseInt("8.0"));
        assertEquals(21, NumberUtils.parseInt("21.0"));
        assertEquals(25, NumberUtils.parseInt("25.0"));
    }

    @Test
    public void testParseIntWithStandardIntegers() {
        // Test standard integer values
        assertEquals(29, NumberUtils.parseInt("29"));
        assertEquals(12, NumberUtils.parseInt("12"));
        assertEquals(10, NumberUtils.parseInt("10"));
        assertEquals(0, NumberUtils.parseInt("0"));
        assertEquals(-5, NumberUtils.parseInt("-5"));
        assertEquals(1000, NumberUtils.parseInt("1000"));
    }

    @Test
    public void testParseIntWithTrimmedValues() {
        // Test values with whitespace
        assertEquals(29, NumberUtils.parseInt(" 29.0 "));
        assertEquals(12, NumberUtils.parseInt("  12  "));
        assertEquals(10, NumberUtils.parseInt("\t10.0\n"));
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithNonIntegerDecimals() {
        // Test decimal values that don't equal integers
        NumberUtils.parseInt("29.5");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithNonIntegerDecimals2() {
        NumberUtils.parseInt("12.7");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithNonIntegerDecimals3() {
        NumberUtils.parseInt("10.1");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithNonIntegerDecimals4() {
        NumberUtils.parseInt("5.99");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithNull() {
        NumberUtils.parseInt(null);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithEmptyString() {
        NumberUtils.parseInt("");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithWhitespaceOnly() {
        NumberUtils.parseInt("   ");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithInvalidInput() {
        NumberUtils.parseInt("abc");
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntWithMixedInput() {
        NumberUtils.parseInt("29abc");
    }

    @Test
    public void testParseIntOrNull() {
        // Test null/empty handling
        assertNull(NumberUtils.parseIntOrNull(null));
        assertNull(NumberUtils.parseIntOrNull(""));
        assertNull(NumberUtils.parseIntOrNull("   "));
        
        // Test valid values
        assertEquals((Integer) 29, NumberUtils.parseIntOrNull("29.0"));
        assertEquals((Integer) 12, NumberUtils.parseIntOrNull("12"));
    }

    @Test
    public void testParseIntOrDefault() {
        // Test default value handling
        assertEquals(42, NumberUtils.parseIntOrDefault("invalid", 42));
        assertEquals(42, NumberUtils.parseIntOrDefault(null, 42));
        assertEquals(42, NumberUtils.parseIntOrDefault("", 42));
        
        // Test valid values
        assertEquals(29, NumberUtils.parseIntOrDefault("29.0", 42));
        assertEquals(12, NumberUtils.parseIntOrDefault("12", 42));
    }

    @Test
    public void testEdgeCases() {
        // Test edge cases
        assertEquals(0, NumberUtils.parseInt("0.0"));
        assertEquals(1, NumberUtils.parseInt("1.0"));
        assertEquals(999, NumberUtils.parseInt("999.0"));
        assertEquals(-1, NumberUtils.parseInt("-1.0"));
        assertEquals(-999, NumberUtils.parseInt("-999.0"));
    }

    @Test
    public void testScientificNotation() {
        // Test scientific notation that equals integers
        assertEquals(100, NumberUtils.parseInt("1.0E2"));
        assertEquals(1000, NumberUtils.parseInt("1.0E3"));
        assertEquals(100, NumberUtils.parseInt("1E2"));
    }
}
