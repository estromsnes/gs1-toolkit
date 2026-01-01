package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.*;
import no.nofuzz.gs1.exception.*;
import no.nofuzz.gs1.model.*;

import java.util.HashMap;
import java.util.Map;

public class Gs1Parser {

    private final AiRegistry registry;
    private final Gs1ComplianceMode mode;
    private final Tokenizer tokenizer;

    public static Gs1Parser defaultParser() {
        return new Gs1Parser(
                StandardAis.defaultRegistry(),
                Gs1ComplianceMode.LENIENT
        );
    }

    public static Gs1Parser strictParser() {
        return new Gs1Parser(
                StandardAis.defaultRegistry(),
                Gs1ComplianceMode.STRICT
        );
    }

    public Gs1Parser(AiRegistry registry, Gs1ComplianceMode mode) {
        this.registry = registry;
        this.mode = mode;
        this.tokenizer = new Gs1Tokenizer(registry, mode);
    }

    public Gs1Result parse(String input) {
        Map<String, Gs1Element> result = new HashMap<>();

        for (Gs1Token token : tokenizer.tokenize(input)) {
            var ai = registry.find(token.ai()).orElseThrow();

            try {
                Object value = ai.parse(token.raw(), mode == Gs1ComplianceMode.STRICT);
                result.put(token.ai(), new Gs1Element(token.ai(), value));
            } catch (Exception e) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.VALUE_PARSE_ERROR,
                        e.getMessage(),
                        token.pos()
                );
            }
        }
        return new Gs1Result(result);
    }
}
