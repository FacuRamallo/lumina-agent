package com.facundo.lumina.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtlService {
    private static final Logger log = LoggerFactory.getLogger(EtlService.class);
    private final VectorStore vectorStore;

    public EtlService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestPdf(Resource pdfResource) {
        log.info("🚀 Iniciando proceso ETL para: {}", pdfResource.getFilename());

        // 1. EXTRACT: Leer el PDF
        // Usamos configuración por defecto (extrae texto plano)
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageBottomMargin(0)
                        .build());

        List<Document> rawDocuments = reader.get();
        log.info("📄 PDF leído. Páginas extraídas: {}", rawDocuments.size());

        // 2. TRANSFORM: Chunking (Dividir en trozos semánticos)
        // Default: chunks de ~800 tokens, con solapamiento (overlap) para no perder contexto entre cortes
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunkedDocuments = splitter.apply(rawDocuments);

        log.info("🧩 Documento dividido en {} chunks (trozos). Generando embeddings...", chunkedDocuments.size());

        // 3. LOAD: Generar Embeddings y Guardar en Postgres
        // Esta línea hace la magia: llama a Ollama (nomic-embed-text) para cada chunk y guarda en BD
        vectorStore.add(chunkedDocuments);

        log.info("✅ Ingesta completada. Datos guardados en PGVector.");
    }
}
