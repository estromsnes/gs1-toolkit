package no.nofuzz.gs1.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Gs1CliSmokeTest {

    @Test
    void cliClassLoads() {
        assertThat(Gs1Cli.class).isNotNull();
    }
}