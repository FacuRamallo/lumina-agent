package com.facundo.lumina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Configuration
public class FileSystemTools {
    private static final Logger log = LoggerFactory.getLogger(FileSystemTools.class);
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    // 1. Definimos la estructura de entrada (Request) como un Record.
    // El nombre de los campos importa: se convierten en la descripción del parámetro para el LLM.
    public record FileReaderRequest(String relativePath) {}

    // 2. Definimos el Bean de la función.
    // La anotación @Description es CRÍTICA: es el "prompt" que lee el LLM para saber CUÁNDO usar esto.
    @Bean
    @Description("DISK ACCESS: Read the ACTUAL content of a local file. Use this ALWAYS whenever you are asked to analyze code, versions, or files. DO NOT hallucinate code; read the file.")
    public Function<FileReaderRequest, String> readFile() {
        return request -> {
            log.info("🛠️ AGENTE INVOCANDO TOOL: readFile con path '{}'", request.relativePath());

            try {
                Path path = Paths.get(PROJECT_ROOT, request.relativePath());

                // Seguridad básica: evitar Path Traversal (salir del directorio del proyecto)
                if (!path.normalize().startsWith(Paths.get(PROJECT_ROOT))) {
                    return "ERROR: Acceso denegado fuera del directorio del proyecto.";
                }

                if (!Files.exists(path)) {
                    return "ERROR: El archivo no existe en la ruta: " + request.relativePath();
                }

                return Files.readString(path);
            } catch (IOException e) {
                log.error("Error leyendo archivo", e);
                return "ERROR IO: " + e.getMessage();
            }
        };
    }
}
