package no.nofuzz.gs1.ai;

/**
 * Utility for calculating and validating GS1 check digits.
 *
 * <p>GS1 check digit algorithm (modulo 10):
 * <ol>
 *   <li>Starting from the right (excluding check digit), multiply each digit alternately by 3 and 1</li>
 *   <li>Sum all the results</li>
 *   <li>Subtract from nearest equal or higher multiple of 10</li>
 * </ol>
 *
 * <p>Example for GTIN-14 "09501101530003":
 * <pre>
 * Digits (excluding check):  0 9 5 0 1 1 0 1 5 3 0 0 0
 * Weights (right to left):   1 3 1 3 1 3 1 3 1 3 1 3 1
 * Products:                  0+27+5+0+1+3+0+3+5+9+0+0+0 = 57
 * Check digit: (10 - (57 % 10)) % 10 = (10 - 7) % 10 = 3 ✓
 * </pre>
 */
public final class Gs1CheckDigit {

    private Gs1CheckDigit() {}

    /**
     * Calculates the GS1 check digit for the given number.
     *
     * @param value the numeric string (without check digit)
     * @return the calculated check digit (0-9)
     * @throws IllegalArgumentException if value is not numeric
     */
    public static int calculate(String value) {
        if (!value.matches("\\d+")) {
            throw new IllegalArgumentException("Value must be numeric");
        }

        int sum = 0;
        int weight = 3;  // Start with weight 3 for rightmost digit

        // Process digits from right to left
        for (int i = value.length() - 1; i >= 0; i--) {
            int digit = value.charAt(i) - '0';
            sum += digit * weight;
            weight = (weight == 3) ? 1 : 3;  // Alternate between 3 and 1
        }

        return (10 - (sum % 10)) % 10;
    }

    /**
     * Validates that the check digit of the given GTIN is correct.
     *
     * @param gtin the complete GTIN including check digit
     * @return true if check digit is valid, false otherwise
     * @throws IllegalArgumentException if GTIN is not numeric or empty
     */
    public static boolean validate(String gtin) {
        if (gtin == null || gtin.isEmpty()) {
            throw new IllegalArgumentException("GTIN cannot be null or empty");
        }
        if (!gtin.matches("\\d+")) {
            throw new IllegalArgumentException("GTIN must be numeric");
        }

        // Extract all digits except the last one (check digit)
        String withoutCheck = gtin.substring(0, gtin.length() - 1);
        int actualCheckDigit = gtin.charAt(gtin.length() - 1) - '0';
        int expectedCheckDigit = calculate(withoutCheck);

        return actualCheckDigit == expectedCheckDigit;
    }
}
