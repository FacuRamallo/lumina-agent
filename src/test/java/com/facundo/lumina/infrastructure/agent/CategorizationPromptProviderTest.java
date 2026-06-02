package com.facundo.lumina.infrastructure.agent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategorizationPromptProviderTest {

    private final CategorizationPromptProvider provider = new CategorizationPromptProvider();

    @Test
    void promptContainsAllCategoryNames() {
        String prompt = provider.prompt();

        assertThat(prompt)
                .contains("GROCERIES")
                .contains("UTILITIES")
                .contains("ENTERTAINMENT")
                .contains("TRANSPORT")
                .contains("INTERNAL_TRANSFER")
                .contains("UNKNOWN");
    }

    @Test
    void promptContainsVendorRules() {
        String prompt = provider.prompt();

        assertThat(prompt)
                .contains("Transferwise")
                .contains("Mercadona")
                .contains("Netflix")
                .contains("Spotify");
    }

    @Test
    void promptContainsDoubleCountingGuardrail() {
        String prompt = provider.prompt();

        assertThat(prompt).contains("double-counting");
    }

    @Test
    void promptContainsOutputFormatInstruction() {
        String prompt = provider.prompt();

        assertThat(prompt).contains("valid JSON");
    }
}
