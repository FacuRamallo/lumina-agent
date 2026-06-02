package com.facundo.lumina.application;

import com.facundo.lumina.domain.Category;
import com.facundo.lumina.domain.DeduplicationId;
import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.domain.Transaction;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategorizationOrchestratorTest {

    private static final SourceSystem SOURCE = new SourceSystem("BANK");

    private static RawTransaction raw(String description) {
        return RawTransaction.builder()
                .date(LocalDate.of(2026, 1, 1))
                .description(description)
                .amount(new BigDecimal("10.00"))
                .rawData(description)
                .build();
    }

    private static ProcessedTransaction processed() {
        return mock(ProcessedTransaction.class);
    }

    @Test
    void process_threRaws_oneBatchCall_returnsThreeProcessedTransactions() {
        DomainProcessor domainProcessor = mock(DomainProcessor.class);
        BatchCategorizationPort batchPort = mock(BatchCategorizationPort.class);
        CategorizationOrchestrator orchestrator = new CategorizationOrchestrator(domainProcessor, batchPort);

        List<RawTransaction> raws = List.of(raw("MERCADONA"), raw("NETFLIX"), raw("UBER"));
        List<Category> categories = List.of(Category.GROCERIES, Category.ENTERTAINMENT, Category.TRANSPORT);
        ProcessedTransaction p1 = processed();
        ProcessedTransaction p2 = processed();
        ProcessedTransaction p3 = processed();

        when(batchPort.categorize(anyList())).thenReturn(categories);
        when(domainProcessor.process(raws.get(0), SOURCE, Category.GROCERIES)).thenReturn(p1);
        when(domainProcessor.process(raws.get(1), SOURCE, Category.ENTERTAINMENT)).thenReturn(p2);
        when(domainProcessor.process(raws.get(2), SOURCE, Category.TRANSPORT)).thenReturn(p3);

        List<ProcessedTransaction> result = orchestrator.process(raws, SOURCE);

        assertThat(result).containsExactly(p1, p2, p3);
        verify(batchPort, times(1)).categorize(anyList());
        verify(domainProcessor, times(3)).process(any(), eq(SOURCE), any());
    }

    @Test
    void process_sevenRaws_twoBatchCalls_returnsSevenProcessedTransactions() {
        DomainProcessor domainProcessor = mock(DomainProcessor.class);
        BatchCategorizationPort batchPort = mock(BatchCategorizationPort.class);
        CategorizationOrchestrator orchestrator = new CategorizationOrchestrator(domainProcessor, batchPort);

        List<RawTransaction> raws = List.of(
                raw("A"), raw("B"), raw("C"), raw("D"), raw("E"), raw("F"), raw("G")
        );
        List<Category> firstBatch = List.of(
                Category.GROCERIES, Category.ENTERTAINMENT, Category.TRANSPORT,
                Category.UTILITIES, Category.INTERNAL_TRANSFER
        );
        List<Category> secondBatch = List.of(Category.UNKNOWN, Category.GROCERIES);

        when(batchPort.categorize(anyList()))
                .thenReturn(firstBatch)
                .thenReturn(secondBatch);
        when(domainProcessor.process(any(), eq(SOURCE), any())).thenReturn(processed());

        List<ProcessedTransaction> result = orchestrator.process(raws, SOURCE);

        assertThat(result).hasSize(7);
        verify(batchPort, times(2)).categorize(anyList());
        verify(domainProcessor, times(7)).process(any(), eq(SOURCE), any());
    }
}
