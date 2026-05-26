package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringAiCategorizationAdapterTest {

    private static SpringAiCategorizationAdapter adapterWith(String llmResponse) {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(llmResponse);

        return new SpringAiCategorizationAdapter(chatClient);
    }

    @Test
    void categorize_supermarketDescription_returnsGroceries() {
        SpringAiCategorizationAdapter adapter = adapterWith("{\"category\": \"GROCERIES\"}");

        Category result = adapter.categorize("SUPERMERCADO CARREFOUR 14/05");

        assertEquals(Category.GROCERIES, result);
    }

    @Test
    void categorize_ownAccountTransfer_returnsInternalTransfer() {
        SpringAiCategorizationAdapter adapter = adapterWith("{\"category\": \"INTERNAL_TRANSFER\"}");

        Category result = adapter.categorize("TRANSFERENCIA A CUENTA PROPIA");

        assertEquals(Category.INTERNAL_TRANSFER, result);
    }

    @Test
    void categorize_unrecognizedDescription_returnsUnknown() {
        SpringAiCategorizationAdapter adapter = adapterWith("{\"category\": \"UNKNOWN\"}");

        Category result = adapter.categorize("XYZ RANDOM PAYMENT 99999");

        assertEquals(Category.UNKNOWN, result);
    }

    @Test
    void categorize_streamingService_returnsEntertainment() {
        SpringAiCategorizationAdapter adapter = adapterWith("{\"category\": \"ENTERTAINMENT\"}");

        Category result = adapter.categorize("NETFLIX MONTHLY SUBSCRIPTION");

        assertEquals(Category.ENTERTAINMENT, result);
    }

    @Test
    void categorize_publicTransport_returnsTransport() {
        SpringAiCategorizationAdapter adapter = adapterWith("{\"category\": \"TRANSPORT\"}");

        Category result = adapter.categorize("SUBE RECARGA SUBTE LINEA D");

        assertEquals(Category.TRANSPORT, result);
    }
}
