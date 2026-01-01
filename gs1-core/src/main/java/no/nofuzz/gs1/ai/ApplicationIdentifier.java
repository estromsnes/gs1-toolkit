package no.nofuzz.gs1.ai;

import java.util.function.Function;

public record ApplicationIdentifier(
        String code,
        Integer fixedLength,
        Integer maxLength,
        boolean variableLength,
        Function<String, Object> valueParser
) {

    public Object parse(String raw, boolean strict) {
        if (fixedLength != null && raw.length() != fixedLength) {
            throw new IllegalArgumentException(
                    "Expected length " + fixedLength + " but got " + raw.length()
            );
        }

        if (strict && maxLength != null && raw.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Value exceeds max length " + maxLength
            );
        }

        return valueParser.apply(raw);
    }
}