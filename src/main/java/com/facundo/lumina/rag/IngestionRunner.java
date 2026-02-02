package com.facundo.lumina.rag;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class IngestionRunner implements CommandLineRunner {
    private final EtlService etlService;

    public IngestionRunner(EtlService etlService) {
        this.etlService = etlService;
    }

    @Override
    public void run(String... args) {
        // Ejecución condicional: Solo si existe el archivo
        // En producción esto sería un endpoint REST o un job programado
        try {
            Resource pdf = new ClassPathResource("docs/manual.pdf");
            if (pdf.exists()) {
                System.out.println("--- 📥 CARGANDO CONOCIMIENTO (RAG) ---");
                etlService.ingestPdf(pdf);
            } else {
                System.out.println("⚠️ No se encontró 'docs/manual.pdf'. Saltando ingesta.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
