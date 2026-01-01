package no.nofuzz.gs1.ai;

import java.util.Map;
import java.util.Optional;

public class AiRegistry {

    private final Map<String, ApplicationIdentifier> registry;

    public AiRegistry(Map<String, ApplicationIdentifier> registry) {
        this.registry = Map.copyOf(registry);
    }

    public Optional<ApplicationIdentifier> find(String ai) {
        return Optional.ofNullable(registry.get(ai));
    }
}