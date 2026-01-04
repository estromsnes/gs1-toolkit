package no.nofuzz.gs1.ai;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public final class StandardAis {

    private static final DateTimeFormatter YYMMDD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private StandardAis() {}

    /**
     * Parses a GS1 date in YYMMDD format with century windowing.
     * <p>
     * GS1 century windowing rule:
     * - Years 00-50 are interpreted as 2000-2050
     * - Years 51-99 are interpreted as 1951-1999
     *
     * @param value the date string in YYMMDD format
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if the date format is invalid
     */
    private static LocalDate parseYYMMDD(String value) {
        try {
            // Parse date components manually to apply GS1 century windowing
            int yy = Integer.parseInt(value.substring(0, 2));
            int mm = Integer.parseInt(value.substring(2, 4));
            int dd = Integer.parseInt(value.substring(4, 6));

            // Apply GS1 century windowing rule
            int year;
            if (yy <= 50) {
                year = 2000 + yy;  // 00-50 = 2000-2050
            } else {
                year = 1900 + yy;  // 51-99 = 1951-1999
            }

            return LocalDate.of(year, mm, dd);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid date format: " + value + " (expected YYMMDD)", e
            );
        }
    }

    /**
     * Parses a variable measure AI value with implied decimal places.
     * <p>
     * The last digit of the AI code indicates decimal places (0-5).
     * For example, AI 3101 has 1 decimal place, so "123456" becomes 12345.6
     *
     * @param aiCode the full AI code (e.g., "3101")
     * @param value the numeric value string
     * @return the parsed value as a String with decimal point inserted
     */
    private static String parseVariableMeasure(String aiCode, String value) {
        // Extract decimal places from last digit of AI code
        int decimalPlaces = Character.getNumericValue(aiCode.charAt(aiCode.length() - 1));

        if (decimalPlaces == 0) {
            // No decimal point needed - strip leading zeros
            return value.replaceFirst("^0+(?!$)", "");
        }

        // Insert decimal point
        int len = value.length();
        if (len <= decimalPlaces) {
            // Pad with leading zeros if needed
            return "0." + "0".repeat(decimalPlaces - len) + value;
        }

        int splitPos = len - decimalPlaces;
        String intPart = value.substring(0, splitPos);
        String decPart = value.substring(splitPos);

        // Strip leading zeros from integer part, but keep at least one digit
        intPart = intPart.replaceFirst("^0+(?!$)", "");
        if (intPart.isEmpty()) {
            intPart = "0";
        }

        return intPart + "." + decPart;
    }

    public static AiRegistry defaultRegistry() {
        return new AiRegistry(Map.ofEntries(

                // SSCC (Serial Shipping Container Code)
                Map.entry("00", new ApplicationIdentifier(
                        "00", 18, 18, false, CharacterSet.NUMERIC, false, v -> v)),

                // GTIN (Global Trade Item Number) - with check digit validation
                Map.entry("01", new ApplicationIdentifier(
                        "01", 14, 14, false, CharacterSet.NUMERIC, true, v -> v)),

                // GTIN of Contained Trade Items
                Map.entry("02", new ApplicationIdentifier(
                        "02", 14, 14, false, CharacterSet.NUMERIC, false, v -> v)),

                // Batch/Lot Number
                Map.entry("10", new ApplicationIdentifier(
                        "10", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Production Date
                Map.entry("11", new ApplicationIdentifier(
                        "11", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Due Date (for payments, deliveries)
                Map.entry("12", new ApplicationIdentifier(
                        "12", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Packaging Date
                Map.entry("13", new ApplicationIdentifier(
                        "13", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Best Before Date
                Map.entry("15", new ApplicationIdentifier(
                        "15", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Sell By Date
                Map.entry("16", new ApplicationIdentifier(
                        "16", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Expiry Date
                Map.entry("17", new ApplicationIdentifier(
                        "17", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Product Variant
                Map.entry("20", new ApplicationIdentifier(
                        "20", 2, 2, false, CharacterSet.NUMERIC, false, v -> v)),

                // Serial Number
                Map.entry("21", new ApplicationIdentifier(
                        "21", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Count of Items
                Map.entry("30", new ApplicationIdentifier(
                        "30", null, 8, true, CharacterSet.NUMERIC, false, Integer::parseInt)),

                // Count of Trade Items
                Map.entry("37", new ApplicationIdentifier(
                        "37", null, 8, true, CharacterSet.NUMERIC, false, Integer::parseInt)),

                // Customer Purchase Order Number
                Map.entry("400", new ApplicationIdentifier(
                        "400", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Ship To - Deliver To Global Location Number (GLN) - with check digit validation
                Map.entry("410", new ApplicationIdentifier(
                        "410", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Bill To - Invoice To Global Location Number (GLN) - with check digit validation
                Map.entry("411", new ApplicationIdentifier(
                        "411", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Ship To - Deliver To Postal Code
                Map.entry("420", new ApplicationIdentifier(
                        "420", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // National Healthcare Reimbursement Number (NHRN)
                Map.entry("710", new ApplicationIdentifier(
                        "710", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Variable Measure Trade Items - Net Weight (kg)
                // Last digit indicates decimal places (0-5)
                Map.entry("3100", new ApplicationIdentifier(
                        "3100", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3100", v))),
                Map.entry("3101", new ApplicationIdentifier(
                        "3101", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3101", v))),
                Map.entry("3102", new ApplicationIdentifier(
                        "3102", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3102", v))),
                Map.entry("3103", new ApplicationIdentifier(
                        "3103", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3103", v))),
                Map.entry("3104", new ApplicationIdentifier(
                        "3104", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3104", v))),
                Map.entry("3105", new ApplicationIdentifier(
                        "3105", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3105", v))),

                // Variable Measure Trade Items - Length (m)
                Map.entry("3110", new ApplicationIdentifier(
                        "3110", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3110", v))),
                Map.entry("3111", new ApplicationIdentifier(
                        "3111", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3111", v))),
                Map.entry("3112", new ApplicationIdentifier(
                        "3112", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3112", v))),
                Map.entry("3113", new ApplicationIdentifier(
                        "3113", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3113", v))),
                Map.entry("3114", new ApplicationIdentifier(
                        "3114", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3114", v))),
                Map.entry("3115", new ApplicationIdentifier(
                        "3115", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3115", v))),

                // Variable Measure Trade Items - Net Weight (lb)
                Map.entry("3200", new ApplicationIdentifier(
                        "3200", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3200", v))),
                Map.entry("3201", new ApplicationIdentifier(
                        "3201", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3201", v))),
                Map.entry("3202", new ApplicationIdentifier(
                        "3202", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3202", v))),
                Map.entry("3203", new ApplicationIdentifier(
                        "3203", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3203", v))),
                Map.entry("3204", new ApplicationIdentifier(
                        "3204", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3204", v))),
                Map.entry("3205", new ApplicationIdentifier(
                        "3205", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3205", v))),

                // Variable Measure Trade Items - Length (in)
                Map.entry("3210", new ApplicationIdentifier(
                        "3210", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3210", v))),
                Map.entry("3211", new ApplicationIdentifier(
                        "3211", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3211", v))),
                Map.entry("3212", new ApplicationIdentifier(
                        "3212", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3212", v))),
                Map.entry("3213", new ApplicationIdentifier(
                        "3213", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3213", v))),
                Map.entry("3214", new ApplicationIdentifier(
                        "3214", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3214", v))),
                Map.entry("3215", new ApplicationIdentifier(
                        "3215", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3215", v))),

                // Variable Measure Trade Items - Length (ft)
                Map.entry("3220", new ApplicationIdentifier(
                        "3220", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3220", v))),
                Map.entry("3221", new ApplicationIdentifier(
                        "3221", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3221", v))),
                Map.entry("3222", new ApplicationIdentifier(
                        "3222", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3222", v))),
                Map.entry("3223", new ApplicationIdentifier(
                        "3223", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3223", v))),
                Map.entry("3224", new ApplicationIdentifier(
                        "3224", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3224", v))),
                Map.entry("3225", new ApplicationIdentifier(
                        "3225", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3225", v))),

                // Variable Measure Trade Items - Gross Weight (kg) for Logistics
                Map.entry("3300", new ApplicationIdentifier(
                        "3300", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3300", v))),
                Map.entry("3301", new ApplicationIdentifier(
                        "3301", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3301", v))),
                Map.entry("3302", new ApplicationIdentifier(
                        "3302", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3302", v))),
                Map.entry("3303", new ApplicationIdentifier(
                        "3303", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3303", v))),
                Map.entry("3304", new ApplicationIdentifier(
                        "3304", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3304", v))),
                Map.entry("3305", new ApplicationIdentifier(
                        "3305", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3305", v)))
        ));
    }
}
