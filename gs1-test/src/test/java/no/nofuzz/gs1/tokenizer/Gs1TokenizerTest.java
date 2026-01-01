package no.nofuzz.gs1.tokenizer;

import no.nofuzz.gs1.ai.StandardAis;
import no.nofuzz.gs1.parser.Gs1Tokenizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Gs1TokenizerTest {

    @Test
    void tokenizesParenthesisBasedInput() {
        var tokenizer = new Gs1Tokenizer(StandardAis.defaultRegistry());

        var tokens = tokenizer.tokenize("(01)09501101530003(10)ABC");

        assertThat(tokens).hasSize(2);
        assertThat(tokens.get(0).ai()).isEqualTo("01");
        assertThat(tokens.get(1).raw()).isEqualTo("ABC");
    }
}