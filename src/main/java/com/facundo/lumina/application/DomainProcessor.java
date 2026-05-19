package com.facundo.lumina.application;

import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.domain.service.HashingService;
import com.facundo.lumina.domain.service.TransactionMapper;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import org.springframework.stereotype.Service;

@Service
public class DomainProcessor {

    private final TransactionMapper mapper;
    private final HashingService hasher;

    public DomainProcessor(TransactionMapper mapper, HashingService hasher) {
        this.mapper = mapper;
        this.hasher = hasher;
    }

    public ProcessedTransaction process(RawTransaction raw, SourceSystem sourceSystem) {
        var transaction = mapper.map(raw, sourceSystem);
        var id = hasher.generateId(transaction);
        return new ProcessedTransaction(transaction, id);
    }
}
