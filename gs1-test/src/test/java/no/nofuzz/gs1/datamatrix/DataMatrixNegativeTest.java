package no.nofuzz.gs1.datamatrix;

import no.nofuzz.gs1.exception.Gs1ParseException;
import no.nofuzz.gs1.parser.Gs1Parser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixNegativeTest {

    private static final char FNC1 = 29;

    @Test
    void failsWhenFnc1IsMissingForVariableLengthAi() {
        String input = "10ABC" + "17251231"; // missing FNC1 separator
        assertThatThrownBy(() -> Gs1Parser.defaultParser().parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void failsOnUnknownAiInDatamatrix() {
        String input = "99ABC" + FNC1;
        assertThatThrownBy(() -> Gs1Parser.defaultParser().parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void failsOnTruncatedInput() {
        String input = "17" + "2512"; // expiry expects 6 chars
        assertThatThrownBy(() -> Gs1Parser.defaultParser().parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void failsOnDanglingFnc1() {
        String input = "10ABC" + FNC1 + FNC1;
        assertThatThrownBy(() -> Gs1Parser.defaultParser().parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }
}