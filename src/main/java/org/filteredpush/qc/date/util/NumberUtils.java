/** NumberUtils.java
 * 
 * Copyright 2015-2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.qc.date.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for parsing numeric values that may contain decimal places.
 * This handles cases where integer values are stored as decimals (e.g., "29.0" -> 29).
 */
public class NumberUtils {

    private static final Log logger = LogFactory.getLog(NumberUtils.class);

    /**
     * Parse a string to an integer, handling decimal values gracefully.
     * If the string represents a decimal number that equals an integer (e.g., "29.0"),
     * it will be parsed as that integer. Otherwise, throws NumberFormatException.
     *
     * @param value the string to parse
     * @return the parsed integer value
     * @throws NumberFormatException if the value cannot be parsed as an integer
     */
    public static int parseInt(String value) throws NumberFormatException {
        if (value == null) {
            throw new NumberFormatException("Cannot parse null value");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new NumberFormatException("Cannot parse empty string");
        }

        // Check if it's a decimal or scientific notation that equals an integer
        if (trimmed.contains(".") || trimmed.toUpperCase().contains("E")) {
            try {
                double doubleValue = Double.parseDouble(trimmed);
                int intValue = (int) doubleValue;

                // Verify that the double value equals the integer value
                if (doubleValue == intValue) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Successfully parsed decimal/scientific '" + trimmed + "' as integer " + intValue);
                    }
                    return intValue;
                } else {
                    throw new NumberFormatException("Decimal/scientific value '" + trimmed + "' does not represent an integer");
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cannot parse '" + trimmed + "' as a number");
            }
        }

        // Standard integer parsing
        return Integer.parseInt(trimmed);
    }

    /**
     * Parse a string to an Integer, handling decimal values gracefully.
     * Returns null if the value is null or empty, otherwise delegates to parseInt().
     *
     * @param value the string to parse
     * @return the parsed Integer value, or null if input is null/empty
     * @throws NumberFormatException if the value cannot be parsed as an integer
     */
    public static Integer parseIntOrNull(String value) throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return parseInt(value);
    }

    /**
     * Safely parse a string to an integer, returning a default value on failure.
     *
     * @param value the string to parse
     * @param defaultValue the value to return if parsing fails
     * @return the parsed integer or the default value
     */
    public static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return parseInt(value);
        } catch (NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse '" + value + "' as integer, using default value " + defaultValue);
            }
            return defaultValue;
        }
    }
}
