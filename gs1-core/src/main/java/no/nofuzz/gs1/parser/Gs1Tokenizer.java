package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.*;
import no.nofuzz.gs1.exception.*;

import java.util.ArrayList;
import java.util.List;

public class Gs1Tokenizer implements Tokenizer {

    private static final char FNC1 = 29;
    private static final int MAX_INPUT_LENGTH = 10_000;

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

        if (input.length() > MAX_INPUT_LENGTH) {
            throw new Gs1ParseException(
                    Gs1ErrorCode.INVALID_FORMAT,
                    "Input exceeds maximum length of " + MAX_INPUT_LENGTH + " characters",
                    0
            );
        }

        // Detect format: GS1-128 (parenthesis) vs DataMatrix (FNC1)
        if (input.charAt(0) == '(') {
            return tokenizeParenthesisFormat(input);
        } else {
            return tokenizeDataMatrixFormat(input);
        }
    }

    private List<Gs1Token> tokenizeParenthesisFormat(String input) {
        List<Gs1Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            if (input.charAt(i) != '(') {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Expected '(' at position " + i,
                        i
                );
            }

            i++; // skip '('
            int aiStart = i;

            // Find closing ')'
            while (i < input.length() && input.charAt(i) != ')') {
                i++;
            }

            if (i >= input.length()) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Missing closing ')' for AI",
                        aiStart
                );
            }

            String ai = input.substring(aiStart, i);
            i++; // skip ')'

            // Verify AI exists in registry
            ApplicationIdentifier aiDef = registry.find(ai).orElseThrow(() ->
                    new Gs1ParseException(
                            Gs1ErrorCode.UNKNOWN_AI,
                            "Unknown AI " + ai,
                            aiStart
                    )
            );

            int valueStart = i;

            // Read value until next '(' or end of input
            while (i < input.length() && input.charAt(i) != '(') {
                i++;
            }

            if (valueStart == i) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Empty value for AI " + ai,
                        valueStart
                );
            }

            String value = input.substring(valueStart, i);
            tokens.add(new Gs1Token(ai, value, valueStart));
        }

        return tokens;
    }

    private List<Gs1Token> tokenizeDataMatrixFormat(String input) {
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
            int aiPos = i;
            ApplicationIdentifier aiDef = registry.find(ai).orElseThrow(() ->
                    new Gs1ParseException(
                            Gs1ErrorCode.UNKNOWN_AI,
                            "Unknown AI " + ai,
                            aiPos
                    )
            );

            i += ai.length();
            int start = i;

            // Handle based on AI type
            if (aiDef.fixedLength() != null) {
                // Fixed-length AI - read exactly fixedLength characters
                int requiredLength = aiDef.fixedLength();
                int endPos = start + requiredLength;

                if (endPos > input.length()) {
                    throw new Gs1ParseException(
                            Gs1ErrorCode.INVALID_FORMAT,
                            "Truncated value for AI " + ai + ": expected " + requiredLength + " characters, got " + (input.length() - start),
                            start
                    );
                }

                tokens.add(new Gs1Token(ai, input.substring(start, endPos), start));
                i = endPos;

                // Skip optional FNC1 separator after fixed-length field
                if (i < input.length() && input.charAt(i) == FNC1) {
                    i++;
                }
            } else {
                // Variable-length AI - detect potential AI codes in the value
                int potentialAiPos = -1;
                while (i < input.length() && input.charAt(i) != FNC1) {
                    // Check if we encounter a potential AI code
                    if (potentialAiPos == -1 && couldBeAiStart(input, i)) {
                        potentialAiPos = i;
                    }
                    i++;
                }

                boolean terminatedByFnc1 = i < input.length() && input.charAt(i) == FNC1;

                // If we found a potential AI in the value and no FNC1, this is ambiguous
                if (!terminatedByFnc1 && potentialAiPos != -1) {
                    throw new Gs1ParseException(
                            Gs1ErrorCode.INVALID_FORMAT,
                            "Missing FNC1 after variable-length AI " + ai + " (found potential AI at position " + potentialAiPos + ")",
                            potentialAiPos
                    );
                }

                if (start == i) {
                    throw new Gs1ParseException(
                            Gs1ErrorCode.INVALID_FORMAT,
                            "Empty value for AI " + ai,
                            start
                    );
                }

                tokens.add(new Gs1Token(ai, input.substring(start, i), start));

                if (terminatedByFnc1) {
                    i++;
                }
            }
        }

        return tokens;
    }

    private boolean couldBeAiStart(String input, int pos) {
        // Check if position could be the start of a known AI (2-4 digits)
        for (int len : new int[]{4, 3, 2}) {
            if (pos + len <= input.length()) {
                String candidate = input.substring(pos, pos + len);
                // Check if it's all digits and matches a known AI
                if (candidate.chars().allMatch(Character::isDigit) && registry.find(candidate).isPresent()) {
                    return true;
                }
            }
        }
        return false;
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
