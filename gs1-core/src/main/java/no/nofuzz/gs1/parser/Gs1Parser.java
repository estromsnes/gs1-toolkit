package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.*;
import no.nofuzz.gs1.exception.*;
import no.nofuzz.gs1.model.*;

import java.util.HashMap;
import java.util.Map;

public class Gs1Parser {

    private final AiRegistry registry;
    private final Tokenizer tokenizer;

    public static Gs1Parser defaultParser() {
        AiRegistry r = StandardAis.defaultRegistry();
        return new Gs1Parser(r, new Gs1Tokenizer(r));
    }

    public Gs1Parser(AiRegistry registry, Tokenizer tokenizer) {
        this.registry = registry;
        this.tokenizer = tokenizer;
    }

    public Gs1Result parse(String input) {
        Map<String, Gs1Element> result = new HashMap<>();

        for (Gs1Token t : tokenizer.tokenize(input)) {
            var ai = registry.find(t.ai()).orElseThrow();
            try {
                result.put(t.ai(),
                        new Gs1Element(t.ai(), ai.parse(t.raw())));
            } catch (Exception e) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.VALUE_PARSE_ERROR,
                        e.getMessage(),
                        t.pos()
                );
            }
        }
        return new Gs1Result(result);
    }
}