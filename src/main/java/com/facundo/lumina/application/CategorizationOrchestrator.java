package com.facundo.lumina.application;

import com.facundo.lumina.domain.Category;
import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class CategorizationOrchestrator {

    private static final int BATCH_SIZE = 5;

    private final DomainProcessor domainProcessor;
    private final BatchCategorizationPort batchCategorizationPort;

    public CategorizationOrchestrator(DomainProcessor domainProcessor, BatchCategorizationPort batchCategorizationPort) {
        this.domainProcessor = domainProcessor;
        this.batchCategorizationPort = batchCategorizationPort;
    }

    public List<ProcessedTransaction> process(List<RawTransaction> raws, SourceSystem sourceSystem) {
        List<String> descriptions = extractDescriptions(raws);
        List<Category> categories = categorizeInBatches(descriptions);
        return buildProcessedTransactions(raws, sourceSystem, categories);
    }

    private List<String> extractDescriptions(List<RawTransaction> raws) {
        return raws.stream()
                .map(RawTransaction::getDescription)
                .toList();
    }

    private List<Category> categorizeInBatches(List<String> descriptions) {
        List<Category> allCategories = new ArrayList<>();
        int total = descriptions.size();
        int start = 0;
        while (start < total) {
            int end = Math.min(start + BATCH_SIZE, total);
            List<Category> batch = batchCategorizationPort.categorize(descriptions.subList(start, end));
            allCategories.addAll(batch);
            start = end;
        }
        return allCategories;
    }

    private List<ProcessedTransaction> buildProcessedTransactions(
            List<RawTransaction> raws,
            SourceSystem sourceSystem,
            List<Category> categories) {
        return IntStream.range(0, raws.size())
                .mapToObj(index -> domainProcessor.process(raws.get(index), sourceSystem, categories.get(index)))
                .toList();
    }
}
