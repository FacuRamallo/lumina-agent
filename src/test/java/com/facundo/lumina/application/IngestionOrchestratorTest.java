package com.facundo.lumina.application;

import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.infrastructure.ingestion.BankCsvParser;
import com.facundo.lumina.infrastructure.ingestion.ParserService;
import com.facundo.lumina.infrastructure.ingestion.PluxeCsvParser;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import com.facundo.lumina.infrastructure.ingestion.SourceParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IngestionOrchestratorTest {

    private IngestionOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        Map<String, SourceParser> parsers = new HashMap<>();
        parsers.put("BANK", new BankCsvParser());
        parsers.put("PLUXE", new PluxeCsvParser());
        ParserService parserService = new ParserService(parsers);

        CategorizationOrchestrator categorizationOrchestrator = mock(CategorizationOrchestrator.class);
        when(categorizationOrchestrator.process(anyList(), any(SourceSystem.class))).thenReturn(List.of());

        orchestrator = new IngestionOrchestrator(parserService, categorizationOrchestrator);
    }

    @Test
    void process_shouldCompleteSuccessfullyWithBankSample() {
        InputStream is = getClass().getResourceAsStream("/data/dummy_integration.csv");
        assertDoesNotThrow(() -> orchestrator.process("BANK", is));
    }
}
