package no.nofuzz.gs1.fuzz;

import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.NumericChars;
import no.nofuzz.gs1.exception.Gs1ParseException;
import no.nofuzz.gs1.parser.Gs1Parser;
import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Gs1FuzzTest {

    private final Gs1Parser parser = Gs1Parser.defaultParser();

    @Property
    void randomGarbageShouldNeverCrashParser(@ForAll String randomInput) {
        assertThatThrownBy(() -> parser.parse(randomInput))
            .isInstanceOf(RuntimeException.class);
    }

    @Property
    void randomDatamatrixLikeInputShouldFailGracefully(
            @ForAll @AlphaChars String value
    ) {
        // AI 10 (variable) without FNC1 termination
        String input = "10" + value + "17" + "251231";

        assertThatThrownBy(() -> parser.parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }

    @Property
    void truncatedFixedLengthAiShouldFail(
            @ForAll @IntRange(min = 0, max = 5) int len
    ) {
        String input = "17" + "123456".substring(0, len);

        assertThatThrownBy(() -> parser.parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }

    @Property
    void unknownAiShouldAlwaysFail(
            @ForAll @NumericChars String digits
    ) {
        Assume.that(digits.length() >= 2);

        String ai = digits.substring(0, 2);
        Assume.that(!ai.equals("01") && !ai.equals("10") && !ai.equals("17") && !ai.equals("21") && !ai.equals("30"));

        String input = ai + "ABC";

        assertThatThrownBy(() -> parser.parse(input))
            .isInstanceOf(Gs1ParseException.class);
    }
}