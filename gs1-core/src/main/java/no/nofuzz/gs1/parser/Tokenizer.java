package no.nofuzz.gs1.parser;

import java.util.List;

public interface Tokenizer {
    List<Gs1Token> tokenize(String input);
}