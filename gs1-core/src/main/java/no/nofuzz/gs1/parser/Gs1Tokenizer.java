package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.AiRegistry;
import no.nofuzz.gs1.exception.*;

import java.util.ArrayList;
import java.util.List;

public class Gs1Tokenizer implements Tokenizer {

    private static final char FNC1 = 29;
    private final AiRegistry registry;

    public Gs1Tokenizer(AiRegistry registry) {
        this.registry = registry;
    }

    @Override
    public List<Gs1Token> tokenize(String input) {
        if (input == null || input.isEmpty()) {
            throw new Gs1ParseException(
                    Gs1ErrorCode.INVALID_FORMAT,
                    "Input is empty",
                    0
            );
        }

        List<Gs1Token> tokens = new ArrayList<>();
        int i = 0;
        int length = input.length();

        while (i < length) {

            // =========================
            // GS1-128 style: (AI)value
            // =========================
            if (input.charAt(i) == '(') {
                int end = input.indexOf(')', i);
                if (end < 0) {
                    throw new Gs1ParseException(
                            Gs1ErrorCode.INVALID_FORMAT,
                            "Missing ')' for AI starting at position " + i,
                            i
                    );
                }

                String ai = input.substring(i + 1, end);
                int finalI = i;
                registry.find(ai).orElseThrow(() ->
                        new Gs1ParseException(
                                Gs1ErrorCode.UNKNOWN_AI,
                                "Unknown AI " + ai,
                                finalI
                        )
                );

                i = end + 1;
                int start = i;

                // Read until next '(' or end
                while (i < length && input.charAt(i) != '(') {
                    i++;
                }

                if (start == i) {
                    throw new Gs1ParseException(
                            Gs1ErrorCode.INVALID_FORMAT,
                            "Empty value for AI " + ai,
                            start
                    );
                }

                tokens.add(new Gs1Token(ai, input.substring(start, i), start));
                continue;
            }

            // ======================================
            // GS1 DataMatrix style: AIvalue<FNC1>
            // ======================================

            // Must have at least 2 chars for AI
            if (i + 2 > length) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Truncated AI at position " + i,
                        i
                );
            }

            String ai = input.substring(i, i + 2);

            int finalI1 = i;
            registry.find(ai).orElseThrow(() ->
                    new Gs1ParseException(
                            Gs1ErrorCode.UNKNOWN_AI,
                            "Unknown AI " + ai,
                            finalI1
                    )
            );

            i += 2;
            int start = i;

            // Read until FNC1 or end
            while (i < length && input.charAt(i) != FNC1) {
                i++;
            }

            if (start == i) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Empty value for AI " + ai,
                        start
                );
            }

            tokens.add(new Gs1Token(ai, input.substring(start, i), start));

            // Skip FNC1 if present (but do NOT require it)
            if (i < length && input.charAt(i) == FNC1) {
                i++;
            }
        }

        return tokens;
    }


    private Gs1ParseException error(String msg, int pos) {
        return new Gs1ParseException(Gs1ErrorCode.INVALID_FORMAT, msg, pos);
    }
}