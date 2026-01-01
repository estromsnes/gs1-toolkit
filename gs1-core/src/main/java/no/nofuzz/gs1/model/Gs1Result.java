package no.nofuzz.gs1.model;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Gs1Result {

    private final Map<String, Gs1Element> map;

    public Gs1Result(Map<String, Gs1Element> map) {
        this.map = Map.copyOf(map);
    }

    /**
     * Gets the value for the specified Application Identifier.
     *
     * @param ai the Application Identifier code
     * @return Optional containing the value if present, empty otherwise
     */
    public Optional<Object> get(String ai) {
        Gs1Element element = map.get(ai);
        return element != null ? Optional.of(element.value()) : Optional.empty();
    }

    /**
     * Checks if the result contains the specified Application Identifier.
     *
     * @param ai the Application Identifier code
     * @return true if the AI is present, false otherwise
     */
    public boolean contains(String ai) {
        return map.containsKey(ai);
    }

    /**
     * Gets the value for the specified Application Identifier, or throws an exception if not present.
     *
     * @param ai the Application Identifier code
     * @return the value for the AI
     * @throws IllegalArgumentException if the AI is not present in the result
     */
    public Object getOrThrow(String ai) {
        Gs1Element element = map.get(ai);
        if (element == null) {
            throw new IllegalArgumentException("AI " + ai + " not found in result");
        }
        return element.value();
    }

    public Map<String, Gs1Element> asMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Gs1Result{" +
                map.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue().value())
                        .collect(Collectors.joining(", ")) +
                "}";
    }
}