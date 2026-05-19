package com.facundo.lumina.infrastructure;

import com.facundo.lumina.application.LanguageModelPort;
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
    CommandLineRunner runner(LanguageModelPort languageModelPort) {
        return args -> {
            System.out.println("\n--- 🧠 LUMINA AGENT: LISTO (TOOLS) ---");
            System.out.println("El agente tiene acceso a tu sistema de archivos.");
            System.out.println("Escribe 'exit' para salir.\n");

            // Bucle interactivo simple para probar varias preguntas
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("TÚ > ");
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input)) {
                        break;
                    }

                    String response = languageModelPort.ask(input);

                    System.out.println("LUMINA > " + response + "\n");
                }
            }
        };
    }
}
