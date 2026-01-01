package no.nofuzz.gs1.validation;

import no.nofuzz.gs1.exception.Gs1ParseException;
import no.nofuzz.gs1.model.Gs1Result;
import no.nofuzz.gs1.parser.Gs1Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationTest {

    private final Gs1Parser parser = Gs1Parser.defaultParser();
    private final Gs1Parser strictParser = Gs1Parser.strictParser();

    @Test
    void rejectsDuplicateAis() {
        String input = "(01)12345678901231(10)BATCH(01)98765432109876";
        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class)
                .hasMessageContaining("Duplicate AI");
    }

    @Test
    void rejectsNonNumericGtin() {
        String input = "(01)ABCDEFGHIJKLMN";
        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class)
                .hasMessageContaining("numeric");
    }

    @Test
    void rejectsInvalidCharactersInAlphanumeric() {
        String input = "(10)ABC\u0000DEF";  // null byte
        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void parsesDateWithCenturyWindowing() {
        // Year 99 = 1999
        Gs1Result r1 = parser.parse("(17)991231");
        assertThat(r1.get("17")).hasValue(LocalDate.of(1999, 12, 31));

        // Year 00 = 2000
        Gs1Result r2 = parser.parse("(17)000101");
        assertThat(r2.get("17")).hasValue(LocalDate.of(2000, 1, 1));

        // Year 50 = 2050
        Gs1Result r3 = parser.parse("(17)500630");
        assertThat(r3.get("17")).hasValue(LocalDate.of(2050, 6, 30));

        // Year 51 = 1951
        Gs1Result r4 = parser.parse("(17)511231");
        assertThat(r4.get("17")).hasValue(LocalDate.of(1951, 12, 31));
    }

    @Test
    void getNonExistentAiReturnsEmpty() {
        Gs1Result result = parser.parse("(01)12345678901231");
        assertThat(result.get("10")).isEmpty();
    }

    @Test
    void containsChecksMembership() {
        Gs1Result result = parser.parse("(01)12345678901231(10)BATCH");
        assertThat(result.contains("01")).isTrue();
        assertThat(result.contains("10")).isTrue();
        assertThat(result.contains("17")).isFalse();
    }

    @Test
    void getOrThrowThrowsForMissingAi() {
        Gs1Result result = parser.parse("(01)12345678901231");
        assertThatThrownBy(() -> result.getOrThrow("10"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AI 10 not found");
    }

    @Test
    void rejectsTruncatedFixedLengthAiInDataMatrix() {
        String input = "\u001D01123\u001D10BATCH";  // AI 01 only 3 digits, needs 14
        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class)
                .hasMessageContaining("Truncated")
                .hasMessageContaining("expected 14");
    }

    @Test
    void rejectsValueExceedingMaxLengthInStrictMode() {
        String input = "(10)" + "A".repeat(25);  // Max is 20
        assertThatThrownBy(() -> strictParser.parse(input))
                .isInstanceOf(Gs1ParseException.class)
                .hasMessageContaining("exceeds max");
    }

    @Test
    void parsesNewDateAis() {
        Gs1Result result = parser.parse("(11)250101(13)250201(15)250301");

        assertThat(result.get("11")).hasValue(LocalDate.of(2025, 1, 1));   // Production date
        assertThat(result.get("13")).hasValue(LocalDate.of(2025, 2, 1));   // Packaging date
        assertThat(result.get("15")).hasValue(LocalDate.of(2025, 3, 1));   // Best before
    }

    @Test
    void parsesNewNumericAis() {
        Gs1Result result = parser.parse("(20)01(30)100(37)50");

        assertThat(result.get("20")).hasValue("01");   // Variant
        assertThat(result.get("30")).hasValue(100);    // Count
        assertThat(result.get("37")).hasValue(50);     // Trade item count
    }

    @Test
    void parsesNewAlphanumericAis() {
        Gs1Result result = parser.parse("(400)PO12345(420)12345(710)NHRN123");

        assertThat(result.get("400")).hasValue("PO12345");   // Purchase order
        assertThat(result.get("420")).hasValue("12345");     // Postal code
        assertThat(result.get("710")).hasValue("NHRN123");   // NHRN
    }

    @Test
    void parsesGlnAis() {
        Gs1Result result = parser.parse("(410)1234567890123(411)9876543210987");

        assertThat(result.get("410")).hasValue("1234567890123");   // Ship to
        assertThat(result.get("411")).hasValue("9876543210987");   // Bill to
    }

    @Test
    void rejectsInvalidGlnLength() {
        String input = "(410)123";  // GLN must be 13 digits
        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class)
                .hasMessageContaining("Expected length 13");
    }
}
