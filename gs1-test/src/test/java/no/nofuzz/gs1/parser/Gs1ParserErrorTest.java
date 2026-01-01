package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.exception.Gs1ParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Gs1ParserErrorTest {

    @Test
    void failsOnUnknownAi() {
        assertThatThrownBy(() ->
            Gs1Parser.defaultParser().parse("(99)123")
        ).isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void failsOnInvalidLength() {
        assertThatThrownBy(() ->
            Gs1Parser.defaultParser().parse("(01)123")
        ).isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void truncatedAiShouldFailGracefully() {
        String input = "1"; // only one digit
        assertThatThrownBy(() -> Gs1Parser.defaultParser().parse(input))
                .isInstanceOf(Gs1ParseException.class);
    }

}