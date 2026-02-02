package com.facundo.lumina;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class LuminaAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(LuminaAgentApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ChatClient.Builder builder, VectorStore vectorStore) {
        return args -> {
            // Configuración del Agente Híbrido (Tools + RAG)
            ChatClient agent = builder
                    // 1. Mantenemos las Tools (File System)
                    .defaultFunctions("readFile")
                    // 2. Añadimos el Advisor de RAG
                    .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, new SearchRequest.Builder()
                            .topK(2) // Traer solo los 2 chunks más relevantes (ahorra contexto)
                            .similarityThreshold(0.7)
                            .build() // Solo traer info si es muy parricidal (evita ruido)
                    ))
                    .build();

            System.out.println("\n--- 🧠 LUMINA AGENT: LISTO (RAG + TOOLS) ---");
            System.out.println("El agente tiene acceso a tu PDF y a tu sistema de archivos.");
            System.out.println("Escribe 'exit' para salir.\n");

            // Bucle interactivo simple para probar varias preguntas
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("TÚ > ");
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input)) break;

                    String response = agent.prompt()
                            .user(input)
                            .call()
                            .content();

                    System.out.println("LUMINA > " + response + "\n");
                }
            }
        };
    }
}
