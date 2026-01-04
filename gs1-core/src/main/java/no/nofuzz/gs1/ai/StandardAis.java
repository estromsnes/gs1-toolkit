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

                // Made-to-Order GTIN
                Map.entry("03", new ApplicationIdentifier(
                        "03", 14, 14, false, CharacterSet.NUMERIC, false, v -> v)),

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

                // Consumer Product Variant (CPV)
                Map.entry("22", new ApplicationIdentifier(
                        "22", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Count of Items
                Map.entry("30", new ApplicationIdentifier(
                        "30", null, 8, true, CharacterSet.NUMERIC, false, Integer::parseInt)),

                // Count of Trade Items
                Map.entry("37", new ApplicationIdentifier(
                        "37", null, 8, true, CharacterSet.NUMERIC, false, Integer::parseInt)),

                // Third Party Controlled Extension (TPX)
                Map.entry("235", new ApplicationIdentifier(
                        "235", null, 28, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Additional Product Identification
                Map.entry("240", new ApplicationIdentifier(
                        "240", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Customer Part Number
                Map.entry("241", new ApplicationIdentifier(
                        "241", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Made-to-Order Variation Number
                Map.entry("242", new ApplicationIdentifier(
                        "242", null, 6, true, CharacterSet.NUMERIC, false, v -> v)),

                // Packaging Component Number
                Map.entry("243", new ApplicationIdentifier(
                        "243", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Secondary Serial Number
                Map.entry("250", new ApplicationIdentifier(
                        "250", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Reference to Source Entity
                Map.entry("251", new ApplicationIdentifier(
                        "251", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // GLN Extension Component
                Map.entry("254", new ApplicationIdentifier(
                        "254", null, 20, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Customer Purchase Order Number
                Map.entry("400", new ApplicationIdentifier(
                        "400", null, 30, true, CharacterSet.ALPHANUMERIC, false, v -> v)),

                // Ship To - Deliver To Global Location Number (GLN) - with check digit validation
                Map.entry("410", new ApplicationIdentifier(
                        "410", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Bill To - Invoice To Global Location Number (GLN) - with check digit validation
                Map.entry("411", new ApplicationIdentifier(
                        "411", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Purchased From Global Location Number (GLN) - with check digit validation
                Map.entry("412", new ApplicationIdentifier(
                        "412", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Ship For / Deliver For Global Location Number (GLN) - with check digit validation
                Map.entry("413", new ApplicationIdentifier(
                        "413", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Identification of Physical Location (GLN) - with check digit validation
                Map.entry("414", new ApplicationIdentifier(
                        "414", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Invoicing Party Global Location Number (GLN) - with check digit validation
                Map.entry("415", new ApplicationIdentifier(
                        "415", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

                // Production or Service Location (GLN) - with check digit validation
                Map.entry("416", new ApplicationIdentifier(
                        "416", 13, 13, false, CharacterSet.NUMERIC, true, v -> v)),

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

                // Variable Measure Trade Items - Width (m)
                Map.entry("3120", new ApplicationIdentifier(
                        "3120", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3120", v))),
                Map.entry("3121", new ApplicationIdentifier(
                        "3121", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3121", v))),
                Map.entry("3122", new ApplicationIdentifier(
                        "3122", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3122", v))),
                Map.entry("3123", new ApplicationIdentifier(
                        "3123", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3123", v))),
                Map.entry("3124", new ApplicationIdentifier(
                        "3124", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3124", v))),
                Map.entry("3125", new ApplicationIdentifier(
                        "3125", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3125", v))),

                // Variable Measure Trade Items - Height (m)
                Map.entry("3130", new ApplicationIdentifier(
                        "3130", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3130", v))),
                Map.entry("3131", new ApplicationIdentifier(
                        "3131", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3131", v))),
                Map.entry("3132", new ApplicationIdentifier(
                        "3132", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3132", v))),
                Map.entry("3133", new ApplicationIdentifier(
                        "3133", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3133", v))),
                Map.entry("3134", new ApplicationIdentifier(
                        "3134", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3134", v))),
                Map.entry("3135", new ApplicationIdentifier(
                        "3135", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3135", v))),

                // Variable Measure Trade Items - Area (m²)
                Map.entry("3140", new ApplicationIdentifier(
                        "3140", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3140", v))),
                Map.entry("3141", new ApplicationIdentifier(
                        "3141", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3141", v))),
                Map.entry("3142", new ApplicationIdentifier(
                        "3142", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3142", v))),
                Map.entry("3143", new ApplicationIdentifier(
                        "3143", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3143", v))),
                Map.entry("3144", new ApplicationIdentifier(
                        "3144", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3144", v))),
                Map.entry("3145", new ApplicationIdentifier(
                        "3145", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3145", v))),

                // Variable Measure Trade Items - Volume (liters)
                Map.entry("3150", new ApplicationIdentifier(
                        "3150", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3150", v))),
                Map.entry("3151", new ApplicationIdentifier(
                        "3151", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3151", v))),
                Map.entry("3152", new ApplicationIdentifier(
                        "3152", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3152", v))),
                Map.entry("3153", new ApplicationIdentifier(
                        "3153", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3153", v))),
                Map.entry("3154", new ApplicationIdentifier(
                        "3154", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3154", v))),
                Map.entry("3155", new ApplicationIdentifier(
                        "3155", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3155", v))),

                // Variable Measure Trade Items - Volume (m³)
                Map.entry("3160", new ApplicationIdentifier(
                        "3160", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3160", v))),
                Map.entry("3161", new ApplicationIdentifier(
                        "3161", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3161", v))),
                Map.entry("3162", new ApplicationIdentifier(
                        "3162", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3162", v))),
                Map.entry("3163", new ApplicationIdentifier(
                        "3163", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3163", v))),
                Map.entry("3164", new ApplicationIdentifier(
                        "3164", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3164", v))),
                Map.entry("3165", new ApplicationIdentifier(
                        "3165", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3165", v))),

                // Variable Measure Trade Items - Width (in)
                Map.entry("3240", new ApplicationIdentifier(
                        "3240", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3240", v))),
                Map.entry("3241", new ApplicationIdentifier(
                        "3241", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3241", v))),
                Map.entry("3242", new ApplicationIdentifier(
                        "3242", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3242", v))),
                Map.entry("3243", new ApplicationIdentifier(
                        "3243", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3243", v))),
                Map.entry("3244", new ApplicationIdentifier(
                        "3244", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3244", v))),
                Map.entry("3245", new ApplicationIdentifier(
                        "3245", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3245", v))),

                // Variable Measure Trade Items - Width (ft)
                Map.entry("3250", new ApplicationIdentifier(
                        "3250", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3250", v))),
                Map.entry("3251", new ApplicationIdentifier(
                        "3251", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3251", v))),
                Map.entry("3252", new ApplicationIdentifier(
                        "3252", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3252", v))),
                Map.entry("3253", new ApplicationIdentifier(
                        "3253", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3253", v))),
                Map.entry("3254", new ApplicationIdentifier(
                        "3254", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3254", v))),
                Map.entry("3255", new ApplicationIdentifier(
                        "3255", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3255", v))),

                // Variable Measure Trade Items - Width (yd)
                Map.entry("3260", new ApplicationIdentifier(
                        "3260", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3260", v))),
                Map.entry("3261", new ApplicationIdentifier(
                        "3261", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3261", v))),
                Map.entry("3262", new ApplicationIdentifier(
                        "3262", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3262", v))),
                Map.entry("3263", new ApplicationIdentifier(
                        "3263", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3263", v))),
                Map.entry("3264", new ApplicationIdentifier(
                        "3264", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3264", v))),
                Map.entry("3265", new ApplicationIdentifier(
                        "3265", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3265", v))),

                // Variable Measure Trade Items - Height (in)
                Map.entry("3270", new ApplicationIdentifier(
                        "3270", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3270", v))),
                Map.entry("3271", new ApplicationIdentifier(
                        "3271", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3271", v))),
                Map.entry("3272", new ApplicationIdentifier(
                        "3272", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3272", v))),
                Map.entry("3273", new ApplicationIdentifier(
                        "3273", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3273", v))),
                Map.entry("3274", new ApplicationIdentifier(
                        "3274", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3274", v))),
                Map.entry("3275", new ApplicationIdentifier(
                        "3275", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3275", v))),

                // Variable Measure Trade Items - Height (ft)
                Map.entry("3280", new ApplicationIdentifier(
                        "3280", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3280", v))),
                Map.entry("3281", new ApplicationIdentifier(
                        "3281", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3281", v))),
                Map.entry("3282", new ApplicationIdentifier(
                        "3282", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3282", v))),
                Map.entry("3283", new ApplicationIdentifier(
                        "3283", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3283", v))),
                Map.entry("3284", new ApplicationIdentifier(
                        "3284", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3284", v))),
                Map.entry("3285", new ApplicationIdentifier(
                        "3285", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3285", v))),

                // Variable Measure Trade Items - Height (yd)
                Map.entry("3290", new ApplicationIdentifier(
                        "3290", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3290", v))),
                Map.entry("3291", new ApplicationIdentifier(
                        "3291", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3291", v))),
                Map.entry("3292", new ApplicationIdentifier(
                        "3292", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3292", v))),
                Map.entry("3293", new ApplicationIdentifier(
                        "3293", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3293", v))),
                Map.entry("3294", new ApplicationIdentifier(
                        "3294", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3294", v))),
                Map.entry("3295", new ApplicationIdentifier(
                        "3295", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3295", v))),

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
                        "3305", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3305", v))),

                // Variable Measure Trade Items - Logistic Length (m)
                Map.entry("3310", new ApplicationIdentifier(
                        "3310", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3310", v))),
                Map.entry("3311", new ApplicationIdentifier(
                        "3311", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3311", v))),
                Map.entry("3312", new ApplicationIdentifier(
                        "3312", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3312", v))),
                Map.entry("3313", new ApplicationIdentifier(
                        "3313", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3313", v))),
                Map.entry("3314", new ApplicationIdentifier(
                        "3314", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3314", v))),
                Map.entry("3315", new ApplicationIdentifier(
                        "3315", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3315", v))),

                // Variable Measure Trade Items - Logistic Width (m)
                Map.entry("3320", new ApplicationIdentifier(
                        "3320", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3320", v))),
                Map.entry("3321", new ApplicationIdentifier(
                        "3321", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3321", v))),
                Map.entry("3322", new ApplicationIdentifier(
                        "3322", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3322", v))),
                Map.entry("3323", new ApplicationIdentifier(
                        "3323", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3323", v))),
                Map.entry("3324", new ApplicationIdentifier(
                        "3324", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3324", v))),
                Map.entry("3325", new ApplicationIdentifier(
                        "3325", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3325", v))),

                // Variable Measure Trade Items - Logistic Height (m)
                Map.entry("3330", new ApplicationIdentifier(
                        "3330", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3330", v))),
                Map.entry("3331", new ApplicationIdentifier(
                        "3331", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3331", v))),
                Map.entry("3332", new ApplicationIdentifier(
                        "3332", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3332", v))),
                Map.entry("3333", new ApplicationIdentifier(
                        "3333", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3333", v))),
                Map.entry("3334", new ApplicationIdentifier(
                        "3334", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3334", v))),
                Map.entry("3335", new ApplicationIdentifier(
                        "3335", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3335", v))),

                // Variable Measure Trade Items - Logistic Area (m²)
                Map.entry("3340", new ApplicationIdentifier(
                        "3340", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3340", v))),
                Map.entry("3341", new ApplicationIdentifier(
                        "3341", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3341", v))),
                Map.entry("3342", new ApplicationIdentifier(
                        "3342", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3342", v))),
                Map.entry("3343", new ApplicationIdentifier(
                        "3343", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3343", v))),
                Map.entry("3344", new ApplicationIdentifier(
                        "3344", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3344", v))),
                Map.entry("3345", new ApplicationIdentifier(
                        "3345", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3345", v))),

                // Variable Measure Trade Items - Logistic Volume (liters)
                Map.entry("3350", new ApplicationIdentifier(
                        "3350", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3350", v))),
                Map.entry("3351", new ApplicationIdentifier(
                        "3351", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3351", v))),
                Map.entry("3352", new ApplicationIdentifier(
                        "3352", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3352", v))),
                Map.entry("3353", new ApplicationIdentifier(
                        "3353", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3353", v))),
                Map.entry("3354", new ApplicationIdentifier(
                        "3354", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3354", v))),
                Map.entry("3355", new ApplicationIdentifier(
                        "3355", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3355", v))),

                // Variable Measure Trade Items - Logistic Volume (m³)
                Map.entry("3360", new ApplicationIdentifier(
                        "3360", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3360", v))),
                Map.entry("3361", new ApplicationIdentifier(
                        "3361", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3361", v))),
                Map.entry("3362", new ApplicationIdentifier(
                        "3362", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3362", v))),
                Map.entry("3363", new ApplicationIdentifier(
                        "3363", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3363", v))),
                Map.entry("3364", new ApplicationIdentifier(
                        "3364", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3364", v))),
                Map.entry("3365", new ApplicationIdentifier(
                        "3365", 6, 6, false, CharacterSet.NUMERIC, false, v -> parseVariableMeasure("3365", v)))
        ));
    }
}
