package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.*;
import no.nofuzz.gs1.exception.*;

import java.util.ArrayList;
import java.util.List;

public class Gs1Tokenizer implements Tokenizer {

    private static final char FNC1 = 29;

    private final AiRegistry registry;
    private final Gs1ComplianceMode mode;

    public Gs1Tokenizer(AiRegistry registry, Gs1ComplianceMode mode) {
        this.registry = registry;
        this.mode = mode;
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

        if (mode == Gs1ComplianceMode.STRICT && input.charAt(0) != FNC1) {
            throw new Gs1ParseException(
                    Gs1ErrorCode.INVALID_FORMAT,
                    "GS1 DataMatrix must start with FNC1 in STRICT mode",
                    0
            );
        }

        List<Gs1Token> tokens = new ArrayList<>();
        int i = (input.charAt(0) == FNC1) ? 1 : 0;

        while (i < input.length()) {

            String ai = resolveAi(input, i);
            int finalI = i;
            registry.find(ai).orElseThrow(() ->
                    new Gs1ParseException(
                            Gs1ErrorCode.UNKNOWN_AI,
                            "Unknown AI " + ai,
                            finalI
                    )
            );

            i += ai.length();
            int start = i;

            while (i < input.length() && input.charAt(i) != FNC1) {
                i++;
            }

            boolean terminatedByFnc1 = i < input.length() && input.charAt(i) == FNC1;

            if (start == i) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Empty value for AI " + ai,
                        start
                );
            }

            tokens.add(new Gs1Token(ai, input.substring(start, i), start));

            if (mode == Gs1ComplianceMode.STRICT && !terminatedByFnc1 && i < input.length()) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Missing FNC1 after variable-length AI " + ai,
                        i
                );
            }

            if (terminatedByFnc1) {
                i++;
            }
        }

        return tokens;
    }

    private String resolveAi(String input, int pos) {
        for (int len : new int[]{4, 3, 2}) {
            if (pos + len <= input.length()) {
                String candidate = input.substring(pos, pos + len);
                if (registry.find(candidate).isPresent()) {
                    return candidate;
                }
            }
        }
        throw new Gs1ParseException(
                Gs1ErrorCode.INVALID_FORMAT,
                "Unable to resolve AI at position " + pos,
                pos
        );
    }
}
