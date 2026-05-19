package com.facundo.lumina.infrastructure.agent;

import com.facundo.lumina.application.LanguageModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaLanguageModelAdapter implements LanguageModelPort {

    private final ChatClient chatClient;

    public OllamaLanguageModelAdapter(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String ask(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
