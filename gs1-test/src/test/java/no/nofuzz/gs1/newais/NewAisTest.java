package no.nofuzz.gs1.newais;

import no.nofuzz.gs1.model.Gs1Result;
import no.nofuzz.gs1.parser.Gs1Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for newly added Application Identifiers:
 * - Phase 1: AI 00, 02, 12, 16
 * - Phase 4: Variable measure AIs (3100-3305)
 */
public class NewAisTest {

    private final Gs1Parser parser = Gs1Parser.defaultParser();
    private final Gs1Parser strictParser = Gs1Parser.strictParser();

    // ========================================
    // Phase 1: Core AIs
    // ========================================

    @Test
    public void parsesAI00_SSCC() {
        Gs1Result result = parser.parse("(00)006141411234567897");
        assertEquals("006141411234567897", result.getOrThrow("00"));
    }

    @Test
    public void parsesAI02_GTINContained() {
        Gs1Result result = parser.parse("(02)09501101530003");
        assertEquals("09501101530003", result.getOrThrow("02"));
    }

    @Test
    public void parsesAI12_DueDate() {
        Gs1Result result = parser.parse("(12)250630");
        LocalDate expected = LocalDate.of(2025, 6, 30);
        assertEquals(expected, result.getOrThrow("12"));
    }

    @Test
    public void parsesAI12_DueDate_centuryWindowing() {
        // Year 51 should be interpreted as 1951
        Gs1Result result = parser.parse("(12)510101");
        LocalDate expected = LocalDate.of(1951, 1, 1);
        assertEquals(expected, result.getOrThrow("12"));

        // Year 50 should be interpreted as 2050
        result = parser.parse("(12)501231");
        expected = LocalDate.of(2050, 12, 31);
        assertEquals(expected, result.getOrThrow("12"));
    }

    @Test
    public void parsesAI16_SellByDate() {
        Gs1Result result = parser.parse("(16)250715");
        LocalDate expected = LocalDate.of(2025, 7, 15);
        assertEquals(expected, result.getOrThrow("16"));
    }

    @Test
    public void parsesMultiplePhase1AIs() {
        Gs1Result result = parser.parse("(00)006141411234567897(02)09501101530003(12)250630(16)250715");
        assertEquals("006141411234567897", result.getOrThrow("00"));
        assertEquals("09501101530003", result.getOrThrow("02"));
        assertEquals(LocalDate.of(2025, 6, 30), result.getOrThrow("12"));
        assertEquals(LocalDate.of(2025, 7, 15), result.getOrThrow("16"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Weight (kg)
    // ========================================

    @Test
    public void parsesAI3100_weightKg_noDecimals() {
        Gs1Result result = parser.parse("(3100)123456");
        assertEquals("123456", result.getOrThrow("3100"));
    }

    @Test
    public void parsesAI3101_weightKg_1decimal() {
        Gs1Result result = parser.parse("(3101)123456");
        assertEquals("12345.6", result.getOrThrow("3101"));
    }

    @Test
    public void parsesAI3102_weightKg_2decimals() {
        Gs1Result result = parser.parse("(3102)123456");
        assertEquals("1234.56", result.getOrThrow("3102"));
    }

    @Test
    public void parsesAI3103_weightKg_3decimals() {
        Gs1Result result = parser.parse("(3103)123456");
        assertEquals("123.456", result.getOrThrow("3103"));
    }

    @Test
    public void parsesAI3104_weightKg_4decimals() {
        Gs1Result result = parser.parse("(3104)123456");
        assertEquals("12.3456", result.getOrThrow("3104"));
    }

    @Test
    public void parsesAI3105_weightKg_5decimals() {
        Gs1Result result = parser.parse("(3105)123456");
        assertEquals("1.23456", result.getOrThrow("3105"));
    }

    @Test
    public void parsesAI3102_weightKg_smallValue() {
        // Value smaller than decimal places should add leading zeros
        Gs1Result result = parser.parse("(3102)000045");
        assertEquals("0.45", result.getOrThrow("3102"));
    }

    @Test
    public void parsesAI3105_weightKg_verySmallValue() {
        Gs1Result result = parser.parse("(3105)000001");
        assertEquals("0.00001", result.getOrThrow("3105"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Length (m)
    // ========================================

    @Test
    public void parsesAI3110_lengthM_noDecimals() {
        Gs1Result result = parser.parse("(3110)005000");
        assertEquals("5000", result.getOrThrow("3110"));
    }

    @Test
    public void parsesAI3112_lengthM_2decimals() {
        Gs1Result result = parser.parse("(3112)012345");
        assertEquals("123.45", result.getOrThrow("3112"));
    }

    @Test
    public void parsesAI3115_lengthM_5decimals() {
        Gs1Result result = parser.parse("(3115)000567");
        assertEquals("0.00567", result.getOrThrow("3115"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Weight (lb)
    // ========================================

    @Test
    public void parsesAI3200_weightLb_noDecimals() {
        Gs1Result result = parser.parse("(3200)025000");
        assertEquals("25000", result.getOrThrow("3200"));
    }

    @Test
    public void parsesAI3202_weightLb_2decimals() {
        Gs1Result result = parser.parse("(3202)012750");
        assertEquals("127.50", result.getOrThrow("3202"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Length (in)
    // ========================================

    @Test
    public void parsesAI3210_lengthIn_noDecimals() {
        Gs1Result result = parser.parse("(3210)012000");
        assertEquals("12000", result.getOrThrow("3210"));
    }

    @Test
    public void parsesAI3212_lengthIn_2decimals() {
        Gs1Result result = parser.parse("(3212)048250");
        assertEquals("482.50", result.getOrThrow("3212"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Length (ft)
    // ========================================

    @Test
    public void parsesAI3220_lengthFt_noDecimals() {
        Gs1Result result = parser.parse("(3220)001000");
        assertEquals("1000", result.getOrThrow("3220"));
    }

    @Test
    public void parsesAI3222_lengthFt_2decimals() {
        Gs1Result result = parser.parse("(3222)010500");
        assertEquals("105.00", result.getOrThrow("3222"));
    }

    // ========================================
    // Phase 4: Variable Measure AIs - Gross Weight (kg) Logistic
    // ========================================

    @Test
    public void parsesAI3300_grossWeightKg_noDecimals() {
        Gs1Result result = parser.parse("(3300)050000");
        assertEquals("50000", result.getOrThrow("3300"));
    }

    @Test
    public void parsesAI3302_grossWeightKg_2decimals() {
        Gs1Result result = parser.parse("(3302)050025");
        assertEquals("500.25", result.getOrThrow("3302"));
    }

    @Test
    public void parsesAI3305_grossWeightKg_5decimals() {
        Gs1Result result = parser.parse("(3305)000123");
        assertEquals("0.00123", result.getOrThrow("3305"));
    }

    // ========================================
    // Combined Real-World Scenarios
    // ========================================

    @Test
    public void parsesProductWithWeightAndDates() {
        // Realistic barcode: GTIN + Batch + Weight + Dates
        Gs1Result result = parser.parse("(01)09501101530003(10)LOT42(3102)001250(17)250630(16)250628");

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("LOT42", result.getOrThrow("10"));
        assertEquals("12.50", result.getOrThrow("3102"));  // 12.50 kg
        assertEquals(LocalDate.of(2025, 6, 30), result.getOrThrow("17"));
        assertEquals(LocalDate.of(2025, 6, 28), result.getOrThrow("16"));
    }

    @Test
    public void parsesShippingContainerWithWeight() {
        // SSCC + Contained GTIN + Gross Weight
        Gs1Result result = parser.parse("(00)006141411234567897(02)09501101530003(3302)125000");

        assertEquals("006141411234567897", result.getOrThrow("00"));
        assertEquals("09501101530003", result.getOrThrow("02"));
        assertEquals("1250.00", result.getOrThrow("3302"));  // 1250.00 kg
    }

    @Test
    public void parsesCableWithLength() {
        // Product with length in meters (e.g., cable, wire)
        Gs1Result result = parser.parse("(01)09501101530003(3112)005000(10)BATCH789");

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("50.00", result.getOrThrow("3112"));  // 50.00 meters
        assertEquals("BATCH789", result.getOrThrow("10"));
    }

    @Test
    public void parsesLumberWithLengthInFeet() {
        // US lumber industry uses feet
        Gs1Result result = parser.parse("(01)09501101530003(3222)001600(10)LUMBER-A");

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("16.00", result.getOrThrow("3222"));  // 16.00 feet
        assertEquals("LUMBER-A", result.getOrThrow("10"));
    }
}
