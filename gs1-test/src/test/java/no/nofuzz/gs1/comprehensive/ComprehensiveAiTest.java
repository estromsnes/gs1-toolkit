package no.nofuzz.gs1.comprehensive;

import no.nofuzz.gs1.model.Gs1Result;
import no.nofuzz.gs1.parser.Gs1Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all newly added Application Identifiers:
 * - Phase 2: Core AIs (03, 22, 235, 240, 241, 242, 243, 250, 251, 254)
 * - Phase 3: GLN AIs (412-416)
 * - Phase 4: Complete variable measure coverage (3120-3365)
 */
public class ComprehensiveAiTest {

    private final Gs1Parser parser = Gs1Parser.defaultParser();
    private final Gs1Parser strictParser = Gs1Parser.strictParser();

    // ========================================
    // Phase 2: Core AIs
    // ========================================

    @Test
    public void parsesAI03_MadeToOrderGTIN() {
        Gs1Result result = parser.parse("(03)09501101530003");
        assertEquals("09501101530003", result.getOrThrow("03"));
    }

    @Test
    public void parsesAI22_ConsumerProductVariant() {
        Gs1Result result = parser.parse("(01)09501101530003(22)RED-LARGE");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("RED-LARGE", result.getOrThrow("22"));
    }

    @Test
    public void parsesAI235_ThirdPartyExtension() {
        Gs1Result result = parser.parse("(01)09501101530003(235)CUSTOM123");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("CUSTOM123", result.getOrThrow("235"));
    }

    @Test
    public void parsesAI240_AdditionalProductID() {
        Gs1Result result = parser.parse("(01)09501101530003(240)MFG-INTERNAL-CODE");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("MFG-INTERNAL-CODE", result.getOrThrow("240"));
    }

    @Test
    public void parsesAI241_CustomerPartNumber() {
        Gs1Result result = parser.parse("(01)09501101530003(241)CUST-PART-789");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("CUST-PART-789", result.getOrThrow("241"));
    }

    @Test
    public void parsesAI242_MadeToOrderVariation() {
        Gs1Result result = parser.parse("(03)09501101530003(242)123456");
        assertEquals("09501101530003", result.getOrThrow("03"));
        assertEquals("123456", result.getOrThrow("242"));
    }

    @Test
    public void parsesAI243_PackagingComponentNumber() {
        Gs1Result result = parser.parse("(01)09501101530003(243)PKG-COMP-001");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("PKG-COMP-001", result.getOrThrow("243"));
    }

    @Test
    public void parsesAI250_SecondarySerialNumber() {
        Gs1Result result = parser.parse("(01)09501101530003(21)SN123(250)SN456");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("SN123", result.getOrThrow("21"));
        assertEquals("SN456", result.getOrThrow("250"));
    }

    @Test
    public void parsesAI251_ReferenceToSourceEntity() {
        Gs1Result result = parser.parse("(01)09501101530003(251)SOURCE-REF-789");
        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("SOURCE-REF-789", result.getOrThrow("251"));
    }

    @Test
    public void parsesAI254_GLNExtension() {
        Gs1Result result = parser.parse("(410)0614141123452(254)DOCK-A3");
        assertEquals("0614141123452", result.getOrThrow("410"));
        assertEquals("DOCK-A3", result.getOrThrow("254"));
    }

    // ========================================
    // Phase 3: GLN AIs
    // ========================================

    @Test
    public void parsesAI412_PurchasedFromGLN() {
        Gs1Result result = parser.parse("(412)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("412"));
    }

    @Test
    public void parsesAI413_ShipForDeliverForGLN() {
        Gs1Result result = parser.parse("(413)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("413"));
    }

    @Test
    public void parsesAI414_PhysicalLocationGLN() {
        Gs1Result result = parser.parse("(414)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("414"));
    }

    @Test
    public void parsesAI415_InvoicingPartyGLN() {
        Gs1Result result = parser.parse("(415)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("415"));
    }

    @Test
    public void parsesAI416_ProductionServiceLocationGLN() {
        Gs1Result result = parser.parse("(416)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("416"));
    }

    @Test
    public void parsesMultipleGLNs() {
        Gs1Result result = parser.parse("(410)0614141123452(411)0614141123452(412)0614141123452");
        assertEquals("0614141123452", result.getOrThrow("410"));
        assertEquals("0614141123452", result.getOrThrow("411"));
        assertEquals("0614141123452", result.getOrThrow("412"));
    }

    // ========================================
    // Phase 4: Dimensions - Width
    // ========================================

    @Test
    public void parsesAI3122_WidthMeters() {
        Gs1Result result = parser.parse("(3122)001500");
        assertEquals("15.00", result.getOrThrow("3122")); // 15.00 meters
    }

    @Test
    public void parsesAI3242_WidthInches() {
        Gs1Result result = parser.parse("(3242)004800");
        assertEquals("48.00", result.getOrThrow("3242")); // 48.00 inches
    }

    @Test
    public void parsesAI3252_WidthFeet() {
        Gs1Result result = parser.parse("(3252)001200");
        assertEquals("12.00", result.getOrThrow("3252")); // 12.00 feet
    }

    @Test
    public void parsesAI3262_WidthYards() {
        Gs1Result result = parser.parse("(3262)000500");
        assertEquals("5.00", result.getOrThrow("3262")); // 5.00 yards
    }

    // ========================================
    // Phase 4: Dimensions - Height
    // ========================================

    @Test
    public void parsesAI3132_HeightMeters() {
        Gs1Result result = parser.parse("(3132)000250");
        assertEquals("2.50", result.getOrThrow("3132")); // 2.50 meters
    }

    @Test
    public void parsesAI3272_HeightInches() {
        Gs1Result result = parser.parse("(3272)007200");
        assertEquals("72.00", result.getOrThrow("3272")); // 72.00 inches
    }

    @Test
    public void parsesAI3282_HeightFeet() {
        Gs1Result result = parser.parse("(3282)000800");
        assertEquals("8.00", result.getOrThrow("3282")); // 8.00 feet
    }

    @Test
    public void parsesAI3292_HeightYards() {
        Gs1Result result = parser.parse("(3292)000300");
        assertEquals("3.00", result.getOrThrow("3292")); // 3.00 yards
    }

    // ========================================
    // Phase 4: Area
    // ========================================

    @Test
    public void parsesAI3142_AreaSquareMeters() {
        Gs1Result result = parser.parse("(3142)012500");
        assertEquals("125.00", result.getOrThrow("3142")); // 125.00 m²
    }

    @Test
    public void parsesAI3145_AreaSquareMeters_5Decimals() {
        Gs1Result result = parser.parse("(3145)000123");
        assertEquals("0.00123", result.getOrThrow("3145")); // 0.00123 m²
    }

    // ========================================
    // Phase 4: Volume
    // ========================================

    @Test
    public void parsesAI3152_VolumeLiters() {
        Gs1Result result = parser.parse("(3152)005000");
        assertEquals("50.00", result.getOrThrow("3152")); // 50.00 liters
    }

    @Test
    public void parsesAI3162_VolumeCubicMeters() {
        Gs1Result result = parser.parse("(3162)000350");
        assertEquals("3.50", result.getOrThrow("3162")); // 3.50 m³
    }

    // ========================================
    // Phase 4: Logistic Measures
    // ========================================

    @Test
    public void parsesAI3312_LogisticLength() {
        Gs1Result result = parser.parse("(3312)120000");
        assertEquals("1200.00", result.getOrThrow("3312")); // 1200.00 cm (logistic)
    }

    @Test
    public void parsesAI3322_LogisticWidth() {
        Gs1Result result = parser.parse("(3322)080000");
        assertEquals("800.00", result.getOrThrow("3322")); // 800.00 cm
    }

    @Test
    public void parsesAI3332_LogisticHeight() {
        Gs1Result result = parser.parse("(3332)100000");
        assertEquals("1000.00", result.getOrThrow("3332")); // 1000.00 cm
    }

    @Test
    public void parsesAI3342_LogisticArea() {
        Gs1Result result = parser.parse("(3342)096000");
        assertEquals("960.00", result.getOrThrow("3342")); // 960.00 cm² logistic
    }

    @Test
    public void parsesAI3352_LogisticVolumeLiters() {
        Gs1Result result = parser.parse("(3352)096000");
        assertEquals("960.00", result.getOrThrow("3352")); // 960.00 liters
    }

    @Test
    public void parsesAI3362_LogisticVolumeCubic() {
        Gs1Result result = parser.parse("(3362)000960");
        assertEquals("9.60", result.getOrThrow("3362")); // 9.60 m³
    }

    // ========================================
    // Real-World Complex Scenarios
    // ========================================

    @Test
    public void parsesCompleteShippingContainer() {
        // SSCC + Product GTIN + Weight + Dimensions
        Gs1Result result = parser.parse(
            "(00)006141411234567897" +
            "(02)09501101530003" +
            "(3302)125000" +        // 1250.00 kg
            "(3312)120000" +        // 1200.00 cm length
            "(3322)080000" +        // 800.00 cm width
            "(3332)100000");        // 1000.00 cm height

        assertEquals("006141411234567897", result.getOrThrow("00"));
        assertEquals("09501101530003", result.getOrThrow("02"));
        assertEquals("1250.00", result.getOrThrow("3302"));
        assertEquals("1200.00", result.getOrThrow("3312"));
        assertEquals("800.00", result.getOrThrow("3322"));
        assertEquals("1000.00", result.getOrThrow("3332"));
    }

    @Test
    public void parsesCustomManufacturedProduct() {
        // Made-to-order with customer specs
        Gs1Result result = parser.parse(
            "(03)09501101530003" +
            "(242)123456" +
            "(241)CUST-PART-XYZ" +
            "(22)BLUE-XLARGE");

        assertEquals("09501101530003", result.getOrThrow("03"));
        assertEquals("123456", result.getOrThrow("242"));
        assertEquals("CUST-PART-XYZ", result.getOrThrow("241"));
        assertEquals("BLUE-XLARGE", result.getOrThrow("22"));
    }

    @Test
    public void parsesCompleteSupplyChainTracking() {
        // Full supply chain with all parties
        Gs1Result result = parser.parse(
            "(01)09501101530003" +
            "(21)SN123456" +
            "(410)0614141123452" +  // Ship to
            "(411)0614141123452" +  // Bill to
            "(412)0614141123452" +  // Purchased from
            "(413)0614141123452" +  // Ship for
            "(414)0614141123452" +  // Physical location
            "(415)0614141123452" +  // Invoicing party
            "(416)0614141123452");  // Production location

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("SN123456", result.getOrThrow("21"));
        assertEquals("0614141123452", result.getOrThrow("410"));
        assertEquals("0614141123452", result.getOrThrow("411"));
        assertEquals("0614141123452", result.getOrThrow("412"));
        assertEquals("0614141123452", result.getOrThrow("413"));
        assertEquals("0614141123452", result.getOrThrow("414"));
        assertEquals("0614141123452", result.getOrThrow("415"));
        assertEquals("0614141123452", result.getOrThrow("416"));
    }

    @Test
    public void parsesFabricRoll() {
        // Fabric sold by area
        Gs1Result result = parser.parse(
            "(01)09501101530003" +
            "(22)COTTON-WHITE" +
            "(3122)001500" +    // 15.00 m width
            "(3112)050000" +    // 500.00 m length
            "(3142)075000");    // 750.00 m² area

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("COTTON-WHITE", result.getOrThrow("22"));
        assertEquals("15.00", result.getOrThrow("3122"));
        assertEquals("500.00", result.getOrThrow("3112"));
        assertEquals("750.00", result.getOrThrow("3142"));
    }

    @Test
    public void parsesLiquidProduct() {
        // Product sold by volume
        Gs1Result result = parser.parse(
            "(01)09501101530003" +
            "(17)260630" +
            "(10)BATCH-789" +
            "(3152)005000");    // 50.00 liters

        assertEquals("09501101530003", result.getOrThrow("01"));
        assertEquals("BATCH-789", result.getOrThrow("10"));
        assertEquals("50.00", result.getOrThrow("3152"));
    }
}
