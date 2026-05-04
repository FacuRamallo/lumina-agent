package com.facundo.lumina;

import org.springframework.ai.chat.client.ChatClient;

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
    CommandLineRunner runner(ChatClient.Builder builder) {
        return args -> {
            // Configuración del Agente (Tools)
            ChatClient agent = builder
                    // 1. Mantenemos las Tools (File System)
                    .defaultFunctions("readFile")
                    .build();

            System.out.println("\n--- 🧠 LUMINA AGENT: LISTO (TOOLS) ---");
            System.out.println("El agente tiene acceso a tu sistema de archivos.");
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
