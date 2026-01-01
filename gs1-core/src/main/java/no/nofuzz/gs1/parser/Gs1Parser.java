package no.nofuzz.gs1.parser;

import no.nofuzz.gs1.ai.*;
import no.nofuzz.gs1.exception.*;
import no.nofuzz.gs1.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GS1 barcode parser.
 *
 * <p>Instances are immutable and thread-safe. It is recommended to create
 * a single parser instance and reuse it across threads.
 *
 * <h2>Usage Examples:</h2>
 * <pre>
 * // Default parser (LENIENT mode)
 * Gs1Parser parser = Gs1Parser.defaultParser();
 *
 * // Strict parser
 * Gs1Parser parser = Gs1Parser.strictParser();
 *
 * // Custom parser with builder
 * Gs1Parser parser = Gs1Parser.builder()
 *     .mode(Gs1ComplianceMode.LENIENT)
 *     .registerAi("99", new ApplicationIdentifier(
 *         "99", null, 10, true, CharacterSet.ALPHANUMERIC, v -> v))
 *     .build();
 * </pre>
 *
 * @see #defaultParser()
 * @see #strictParser()
 * @see #builder()
 */
public class Gs1Parser {

    private final AiRegistry registry;
    private final Gs1ComplianceMode mode;
    private final Tokenizer tokenizer;

    /**
     * Creates a parser with default (LENIENT) mode and standard AIs.
     *
     * @return parser with LENIENT mode
     */
    public static Gs1Parser defaultParser() {
        return new Gs1Parser(
                StandardAis.defaultRegistry(),
                Gs1ComplianceMode.LENIENT
        );
    }

    /**
     * Creates a parser with STRICT mode and standard AIs.
     *
     * @return parser with STRICT mode
     */
    public static Gs1Parser strictParser() {
        return new Gs1Parser(
                StandardAis.defaultRegistry(),
                Gs1ComplianceMode.STRICT
        );
    }

    /**
     * Creates a new builder for configuring a custom parser.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public Gs1Parser(AiRegistry registry, Gs1ComplianceMode mode) {
        this.registry = registry;
        this.mode = mode;
        this.tokenizer = new Gs1Tokenizer(registry, mode);
    }

    public Gs1Result parse(String input) {
        Map<String, Gs1Element> result = new HashMap<>();

        for (Gs1Token token : tokenizer.tokenize(input)) {
            // Check for duplicate AIs
            if (result.containsKey(token.ai())) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.INVALID_FORMAT,
                        "Duplicate AI " + token.ai() + " found in input",
                        token.pos()
                );
            }

            var ai = registry.find(token.ai()).orElseThrow();

            try {
                Object value = ai.parse(token.raw(), mode == Gs1ComplianceMode.STRICT);
                result.put(token.ai(), new Gs1Element(token.ai(), value));
            } catch (IllegalArgumentException e) {
                throw new Gs1ParseException(
                        Gs1ErrorCode.VALUE_PARSE_ERROR,
                        "Invalid value for AI " + token.ai() + ": " + e.getMessage(),
                        token.pos()
                );
            }
        }
        return new Gs1Result(result);
    }

    /**
     * Builder for creating customized Gs1Parser instances.
     *
     * <p>Allows configuration of compliance mode and registration of custom
     * Application Identifiers.
     *
     * <p>Example:
     * <pre>
     * Gs1Parser parser = Gs1Parser.builder()
     *     .mode(Gs1ComplianceMode.STRICT)
     *     .registerAi("99", new ApplicationIdentifier(
     *         "99", null, 10, true, CharacterSet.ALPHANUMERIC,
     *         v -> v.toUpperCase()))
     *     .build();
     * </pre>
     */
    public static class Builder {
        private Gs1ComplianceMode mode = Gs1ComplianceMode.LENIENT;
        private final Map<String, ApplicationIdentifier> customAis = new HashMap<>();
        private boolean useStandardAis = true;

        /**
         * Sets the compliance mode for the parser.
         *
         * @param mode the compliance mode (LENIENT or STRICT)
         * @return this builder
         */
        public Builder mode(Gs1ComplianceMode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Registers a custom Application Identifier.
         *
         * <p>If an AI with the same code already exists (either standard or previously
         * registered custom), it will be replaced.
         *
         * @param code the AI code (e.g., "01", "99")
         * @param ai the ApplicationIdentifier definition
         * @return this builder
         */
        public Builder registerAi(String code, ApplicationIdentifier ai) {
            this.customAis.put(code, ai);
            return this;
        }

        /**
         * Disables the standard Application Identifiers.
         *
         * <p>By default, all standard AIs (01, 10, 11, 13, 15, 17, 20, 21, 30, 37,
         * 400, 410, 411, 420, 710) are included. Call this method to start with an
         * empty registry and only use custom AIs.
         *
         * @return this builder
         */
        public Builder withoutStandardAis() {
            this.useStandardAis = false;
            return this;
        }

        /**
         * Enables the standard Application Identifiers (default behavior).
         *
         * @return this builder
         */
        public Builder withStandardAis() {
            this.useStandardAis = true;
            return this;
        }

        /**
         * Builds the configured Gs1Parser instance.
         *
         * @return a new Gs1Parser with the configured settings
         */
        public Gs1Parser build() {
            Map<String, ApplicationIdentifier> allAis = new HashMap<>();

            // Start with standard AIs if enabled
            if (useStandardAis) {
                allAis.putAll(StandardAis.defaultRegistry().asMap());
            }

            // Add/override with custom AIs
            allAis.putAll(customAis);

            AiRegistry registry = new AiRegistry(allAis);
            return new Gs1Parser(registry, mode);
        }
    }
}
