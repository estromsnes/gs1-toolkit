package no.nofuzz.gs1.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiRegistryTest {

    @Test
    void defaultRegistryContainsCoreAis() {
        AiRegistry registry = StandardAis.defaultRegistry();

        assertThat(registry.find("01")).isPresent();
        assertThat(registry.find("10")).isPresent();
        assertThat(registry.find("17")).isPresent();
    }
}