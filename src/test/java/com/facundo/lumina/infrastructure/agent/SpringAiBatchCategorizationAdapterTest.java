package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringAiBatchCategorizationAdapterTest {

    private static SpringAiBatchCategorizationAdapter adapterWith(String llmResponse) {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);
        CategorizationPromptProvider promptProvider = mock(CategorizationPromptProvider.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(llmResponse);
        when(promptProvider.prompt()).thenReturn("taxonomy prompt");

        return new SpringAiBatchCategorizationAdapter(chatClient, promptProvider);
    }

    @Test
    void categorize_twoDescriptions_returnsCorrectCategories() {
        SpringAiBatchCategorizationAdapter adapter = adapterWith(
                "[{\"category\":\"GROCERIES\"},{\"category\":\"ENTERTAINMENT\"}]"
        );

        List<Category> result = adapter.categorize(List.of("MERCADONA", "NETFLIX"));

        assertThat(result).containsExactly(Category.GROCERIES, Category.ENTERTAINMENT);
    }

    @Test
    void categorize_singleUnknownDescription_returnsUnknown() {
        SpringAiBatchCategorizationAdapter adapter = adapterWith(
                "[{\"category\":\"UNKNOWN\"}]"
        );

        List<Category> result = adapter.categorize(List.of("XYZ RANDOM 99999"));

        assertThat(result).containsExactly(Category.UNKNOWN);
    }
}
