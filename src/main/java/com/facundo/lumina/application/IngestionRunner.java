package com.facundo.lumina.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class IngestionRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionRunner.class);
    private final IngestionOrchestrator orchestrator;

    public IngestionRunner(IngestionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public void run(String... args) throws Exception {
        Path inputDir = Paths.get("src/main/resources/data/input/");
        if (!Files.exists(inputDir)) {
            log.warn("Input directory {} does not exist. Skipping ingestion.", inputDir.toAbsolutePath());
            return;
        }

        try (Stream<Path> paths = Files.list(inputDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(this::isCsvFile)
                 .forEach(this::processFile);
        }
    }

    private boolean isCsvFile(Path path) {
        String pathStr = path.toString();
        return pathStr.endsWith(".csv");
    }

    private void processFile(Path filePath) {
        String fileName = extractFileName(filePath);
        String sourceType = extractSourceType(fileName);
        
        try (InputStream is = openStream(filePath)) {
            log.info("Triggering ingestion for file: {}", fileName);
            orchestrator.process(sourceType, is);
        } catch (Exception e) {
            log.error("Failed to process file: {}", fileName, e);
        }
    }

    private String extractFileName(Path filePath) {
        Path namePath = filePath.getFileName();
        return namePath.toString();
    }

    private InputStream openStream(Path filePath) throws Exception {
        java.io.File file = filePath.toFile();
        return new FileInputStream(file);
    }

    private String extractSourceType(String fileName) {
        if (!fileName.contains("_")) {
            return "UNKNOWN";
        }
        int separatorIndex = fileName.indexOf('_');
        String prefix = fileName.substring(0, separatorIndex);
        return prefix.toUpperCase();
    }
}
