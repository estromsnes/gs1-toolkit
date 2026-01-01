package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.model.Gs1Result;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class Gs1ParserHappyPathTest {

    @Test
    void parsesFixedAndVariableLengthAis() {
        Gs1Result result = Gs1Parser.defaultParser()
            .parse("(01)09501101530003(17)251231(10)ABC123");

        assertThat(result.get("01")).hasValue("09501101530003");
        assertThat(result.get("10")).hasValue("ABC123");
        assertThat(result.get("17")).hasValue(LocalDate.of(2025, 12, 31));
    }
}