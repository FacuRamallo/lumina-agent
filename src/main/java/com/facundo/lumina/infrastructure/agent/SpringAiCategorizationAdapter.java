package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.application.CategorizationPort;
import com.facundo.lumina.domain.Category;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class SpringAiCategorizationAdapter implements CategorizationPort {

    private final ChatClient chatClient;
    private final CategorizationPromptProvider promptProvider;

    public SpringAiCategorizationAdapter(ChatClient chatClient, CategorizationPromptProvider promptProvider) {
        this.chatClient = chatClient;
        this.promptProvider = promptProvider;
    }

    @Override
    public Category categorize(String rawDescription) {
        BeanOutputConverter<CategoryResponse> converter = new BeanOutputConverter<>(CategoryResponse.class);
        String response = chatClient.prompt()
                .system(promptProvider.prompt())
                .user(rawDescription + "\n" + converter.getFormat())
                .call()
                .content();
        return converter.convert(response).category();
    }

    record CategoryResponse(Category category) {}
}
