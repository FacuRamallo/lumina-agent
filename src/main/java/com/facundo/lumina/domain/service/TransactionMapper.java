package com.facundo.lumina.domain.service;

import com.facundo.lumina.domain.*;
import com.facundo.lumina.ingestion.RawTransaction;
import java.util.Currency;

public final class TransactionMapper {

    private static final String DEFAULT_CURRENCY = "EUR";
    private static final String DEFAULT_CATEGORY = "Uncategorized";

    public Transaction map(RawTransaction raw, SourceSystem source) {
        return new Transaction(
            createOrigin(raw, source),
            createDetails(raw)
        );
    }

    private TransactionOrigin createOrigin(RawTransaction raw, SourceSystem source) {
        return new TransactionOrigin(
            source,
            new TransactionDate(raw.getDate())
        );
    }

    private TransactionDetails createDetails(RawTransaction raw) {
        return new TransactionDetails(
            createDescription(raw),
            createMoney(raw)
        );
    }

    private TransactionDescription createDescription(RawTransaction raw) {
        return new TransactionDescription(
            new RawDescription(raw.getDescription()),
            new Category(DEFAULT_CATEGORY)
        );
    }

    private Money createMoney(RawTransaction raw) {
        return new Money(
            raw.getAmount(),
            Currency.getInstance(DEFAULT_CURRENCY)
        );
    }
}
