package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.application.CategorizationPort;
import com.facundo.lumina.domain.Category;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class SpringAiCategorizationAdapter implements CategorizationPort {

    private static final String SYSTEM_PROMPT = """
            You are a financial transaction categorizer.
            Analyze the raw transaction description provided by the user and return exactly one category.
            Valid categories: GROCERIES, UTILITIES, ENTERTAINMENT, TRANSPORT, INTERNAL_TRANSFER, UNKNOWN.
            Rules:
            - Use INTERNAL_TRANSFER for any transfer between own accounts to avoid double-counting.
            - Use UNKNOWN only when no other category fits.
            - Return only valid JSON. No extra text, no markdown.
            """;

    private final ChatClient chatClient;
    private final BeanOutputConverter<CategoryResponse> converter;

    public SpringAiCategorizationAdapter(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.converter = new BeanOutputConverter<>(CategoryResponse.class);
    }

    @Override
    public Category categorize(String rawDescription) {
        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(rawDescription + "\n" + converter.getFormat())
                .call()
                .content();
        return converter.convert(response).category();
    }

    record CategoryResponse(Category category) {}
}
