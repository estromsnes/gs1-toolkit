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

    public static AiRegistry defaultRegistry() {
        return new AiRegistry(Map.ofEntries(

                // GTIN (Global Trade Item Number) - with check digit validation
                Map.entry("01", new ApplicationIdentifier(
                        "01", 14, 14, false, CharacterSet.NUMERIC, true, v -> v)),

                // Batch/Lot Number
                Map.entry("10", new ApplicationIdentifier(
                        "10", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Production Date
                Map.entry("11", new ApplicationIdentifier(
                        "11", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Packaging Date
                Map.entry("13", new ApplicationIdentifier(
                        "13", 6, 6, false, CharacterSet.NUMERIC, false,
                        StandardAis::parseYYMMDD)),

                // Best Before Date
                Map.entry("15", new ApplicationIdentifier(
                        "15", 6, 6, false, CharacterSet.NUMERIC, false,
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
                        "710", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v))
        ));
    }
}
