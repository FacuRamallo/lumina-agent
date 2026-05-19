package com.facundo.lumina.application;

import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.infrastructure.ingestion.ParserService;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class IngestionOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(IngestionOrchestrator.class);

    private final ParserService parserService;
    private final DomainProcessor domainProcessor;

    public IngestionOrchestrator(ParserService parserService, DomainProcessor domainProcessor) {
        this.parserService = parserService;
        this.domainProcessor = domainProcessor;
    }

    public void process(String sourceType, InputStream inputStream) {
        List<RawTransaction> rawTransactions = parserService.parse(sourceType, inputStream);
        SourceSystem sourceSystem = new SourceSystem(sourceType);

        for (RawTransaction raw : rawTransactions) {
            ProcessedTransaction processed = domainProcessor.process(raw, sourceSystem);
            logProcessedTransaction(processed);
        }
    }

    private void logProcessedTransaction(ProcessedTransaction processed) {
        String transactionStr = processed.transaction().toString();
        String idStr = processed.id().toString();
        log.info("Processed Transaction: {} with ID: {}", transactionStr, idStr);
    }
}
