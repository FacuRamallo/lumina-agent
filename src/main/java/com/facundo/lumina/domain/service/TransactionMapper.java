package com.facundo.lumina.domain.service;

import com.facundo.lumina.domain.Category;
import com.facundo.lumina.domain.Money;
import com.facundo.lumina.domain.RawDescription;
import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.domain.Transaction;
import com.facundo.lumina.domain.TransactionDate;
import com.facundo.lumina.domain.TransactionDetails;
import com.facundo.lumina.domain.TransactionDescription;
import com.facundo.lumina.domain.TransactionOrigin;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import java.util.Currency;

public final class TransactionMapper {

    private static final String DEFAULT_CURRENCY = "EUR";

    public Transaction map(RawTransaction raw, SourceSystem source, Category category) {
        return new Transaction(
            createOrigin(raw, source),
            createDetails(raw, category)
        );
    }

    private TransactionOrigin createOrigin(RawTransaction raw, SourceSystem source) {
        return new TransactionOrigin(
            source,
            new TransactionDate(raw.getDate())
        );
    }

    private TransactionDetails createDetails(RawTransaction raw, Category category) {
        return new TransactionDetails(
            createDescription(raw, category),
            createMoney(raw)
        );
    }

    private TransactionDescription createDescription(RawTransaction raw, Category category) {
        return new TransactionDescription(
            new RawDescription(raw.getDescription()),
            category
        );
    }

    private Money createMoney(RawTransaction raw) {
        return new Money(
            raw.getAmount(),
            Currency.getInstance(DEFAULT_CURRENCY)
        );
    }
}
