package no.nofuzz.gs1.checkdigit;

import no.nofuzz.gs1.ai.Gs1CheckDigit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Gs1CheckDigitTest {

    @Test
    void calculatesCorrectCheckDigitForGtin14() {
        // Example from GS1 General Specifications
        // GTIN-14: 09501101530003 (check digit = 3)
        String withoutCheck = "0950110153000";
        int checkDigit = Gs1CheckDigit.calculate(withoutCheck);
        assertThat(checkDigit).isEqualTo(3);
    }

    @Test
    void calculatesCorrectCheckDigitForGtin13() {
        // GTIN-13: 5901234123457 (check digit = 7)
        String withoutCheck = "590123412345";
        int checkDigit = Gs1CheckDigit.calculate(withoutCheck);
        assertThat(checkDigit).isEqualTo(7);
    }

    @Test
    void calculatesCorrectCheckDigitForGln() {
        // GLN: 0614141123452 (check digit = 2)
        String withoutCheck = "061414112345";
        int checkDigit = Gs1CheckDigit.calculate(withoutCheck);
        assertThat(checkDigit).isEqualTo(2);
    }

    @Test
    void calculatesCheckDigitZero() {
        // When calculation results in check digit 0 (sum is multiple of 10)
        String withoutCheck = "0000000000000";  // All zeros, sum = 0, check = 0
        int checkDigit = Gs1CheckDigit.calculate(withoutCheck);
        assertThat(checkDigit).isEqualTo(0);
    }

    @Test
    void validatesCorrectGtin() {
        assertThat(Gs1CheckDigit.validate("09501101530003")).isTrue();
        assertThat(Gs1CheckDigit.validate("5901234123457")).isTrue();
        assertThat(Gs1CheckDigit.validate("0614141123452")).isTrue();
    }

    @Test
    void detectsInvalidCheckDigit() {
        // Correct GTIN: 09501101530003, using wrong check digit
        assertThat(Gs1CheckDigit.validate("09501101530004")).isFalse();
        assertThat(Gs1CheckDigit.validate("09501101530000")).isFalse();
        assertThat(Gs1CheckDigit.validate("09501101530009")).isFalse();
    }

    @Test
    void rejectsNonNumericInput() {
        assertThatThrownBy(() -> Gs1CheckDigit.calculate("ABC123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be numeric");

        assertThatThrownBy(() -> Gs1CheckDigit.validate("ABC123456789X"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be numeric");
    }

    @Test
    void rejectsEmptyInput() {
        assertThatThrownBy(() -> Gs1CheckDigit.validate(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> Gs1CheckDigit.validate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }
}
