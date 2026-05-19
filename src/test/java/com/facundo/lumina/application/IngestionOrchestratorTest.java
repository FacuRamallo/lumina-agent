package com.facundo.lumina.application;

import com.facundo.lumina.domain.service.HashingService;
import com.facundo.lumina.domain.service.TransactionMapper;
import com.facundo.lumina.infrastructure.ingestion.BankCsvParser;
import com.facundo.lumina.infrastructure.ingestion.ParserService;
import com.facundo.lumina.infrastructure.ingestion.PluxeCsvParser;
import com.facundo.lumina.infrastructure.ingestion.SourceParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IngestionOrchestratorTest {

    private IngestionOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        Map<String, SourceParser> parsers = new HashMap<>();
        parsers.put("BANK", new BankCsvParser());
        parsers.put("PLUXE", new PluxeCsvParser());
        ParserService parserService = new ParserService(parsers);
        DomainProcessor domainProcessor = new DomainProcessor(
                new TransactionMapper(),
                new HashingService()
        );
        
        orchestrator = new IngestionOrchestrator(
                parserService,
                domainProcessor
        );
    }

    @Test
    void process_shouldCompleteSuccessfullyWithBankSample() {
        InputStream is = getClass().getResourceAsStream("/data/dummy_integration.csv");
        assertDoesNotThrow(() -> orchestrator.process("BANK", is));
    }
}
