package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.exception.Gs1ParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class Gs1StrictParserTest {
    @Test
    void strictModeRequiresLeadingFnc1() {
        Gs1Parser parser = Gs1Parser.strictParser();

        assertThatThrownBy(() ->
                parser.parse("10ABC" + (char)29 + "17251231")
        ).isInstanceOf(Gs1ParseException.class);
    }

    @Test
    void strictModeRequiresFnc1AfterVariableLengthAi() {
        Gs1Parser parser = Gs1Parser.strictParser();

        String input = "" + (char)29 + "10ABC17" + "251231";

        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class);
    }


    @Test
    void strictModeAcceptsValidDatamatrix() {
        Gs1Parser parser = Gs1Parser.strictParser();

        String input = "" + (char)29 +
                "10ABC" + (char)29 +
                "17251231";

        assertThatCode(() -> parser.parse(input))
                .doesNotThrowAnyException();
    }

    @Test
    void strictModeEnforcesMaxLength() {
        Gs1Parser parser = Gs1Parser.strictParser();

        String input = "" + (char)29 +
                "10" + "A".repeat(25) + (char)29;

        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(Gs1ParseException.class);
    }

}
