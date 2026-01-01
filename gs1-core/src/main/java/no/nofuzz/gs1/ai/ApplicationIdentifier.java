package no.nofuzz.gs1.ai;

import java.util.function.Function;

public record ApplicationIdentifier(
        String code,
        int fixedLength,
        boolean variableLength,
        Function<String, Object> valueParser
) {
    public Object parse(String raw) {
        if (!variableLength && raw.length() != fixedLength) {
            throw new IllegalArgumentException(
                "Expected length " + fixedLength + " but got " + raw.length()
            );
        }
        return valueParser.apply(raw);
    }
}