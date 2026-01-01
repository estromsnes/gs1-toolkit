package no.nofuzz.gs1.ai;

import java.util.function.Function;

public record ApplicationIdentifier(
        String code,
        Integer fixedLength,
        Integer maxLength,
        boolean variableLength,
        CharacterSet characterSet,
        Function<String, Object> valueParser
) {

    public Object parse(String raw, boolean strict) {
        // Length validation
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

        // Character set validation
        validateCharacterSet(raw);

        return valueParser.apply(raw);
    }

    private void validateCharacterSet(String value) {
        switch (characterSet) {
            case NUMERIC:
                if (!value.matches("\\d+")) {
                    throw new IllegalArgumentException(
                            "AI " + code + " must contain only numeric characters (0-9), got: " + value
                    );
                }
                break;

            case ALPHANUMERIC:
                // GS1 alphanumeric: 0-9, A-Z, space, and some special chars
                if (!value.matches("[0-9A-Z !\"#$%&'()*+,\\-./:;<=>?_]*")) {
                    throw new IllegalArgumentException(
                            "AI " + code + " contains invalid characters: " + value
                    );
                }
                break;

            case ANY:
                // No validation
                break;
        }
    }
}