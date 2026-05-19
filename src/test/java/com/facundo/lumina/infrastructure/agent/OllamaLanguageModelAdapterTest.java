package com.facundo.lumina.infrastructure.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OllamaLanguageModelAdapterTest {

    @Test
    void ask_delegatesToChatClient_returnsContent() {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("hello")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("world");

        OllamaLanguageModelAdapter adapter = new OllamaLanguageModelAdapter(chatClient);
        String response = adapter.ask("hello");

        assertEquals("world", response);
    }
}
