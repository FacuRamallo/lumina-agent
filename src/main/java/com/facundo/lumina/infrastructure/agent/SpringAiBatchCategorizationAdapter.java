package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.application.BatchCategorizationPort;
import com.facundo.lumina.domain.Category;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SpringAiBatchCategorizationAdapter implements BatchCategorizationPort {

    private final ChatClient chatClient;
    private final CategorizationPromptProvider promptProvider;

    public SpringAiBatchCategorizationAdapter(ChatClient chatClient, CategorizationPromptProvider promptProvider) {
        this.chatClient = chatClient;
        this.promptProvider = promptProvider;
    }

    @Override
    public List<Category> categorize(List<String> descriptions) {
        BeanOutputConverter<List<CategoryResponse>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<>() {}
        );
        String userMessage = buildUserMessage(descriptions, converter.getFormat());
        String response = chatClient.prompt()
                .system(promptProvider.prompt())
                .user(userMessage)
                .call()
                .content();
        return converter.convert(response).stream()
                .map(CategoryResponse::category)
                .toList();
    }

    private String buildUserMessage(List<String> descriptions, String formatInstruction) {
        StringBuilder message = new StringBuilder(
                "Categorize each of the following transaction descriptions IN ORDER.\n" +
                "Return a JSON array with exactly " + descriptions.size() + " objects in the SAME ORDER as the input.\n\n"
        );
        IntStream.range(0, descriptions.size())
                .forEach(index -> message.append(index + 1).append(". ").append(descriptions.get(index)).append("\n"));
        message.append("\n").append(formatInstruction);
        return message.toString();
    }

    record CategoryResponse(Category category) {}
}
