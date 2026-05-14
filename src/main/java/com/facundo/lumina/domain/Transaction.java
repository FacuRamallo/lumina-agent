package com.facundo.lumina.domain;

import java.util.Objects;

public final class Transaction {
    private final TransactionOrigin origin;
    private final TransactionDetails details;

    public Transaction(TransactionOrigin origin, TransactionDetails details) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin cannot be null");
        }
        if (details == null) {
            throw new IllegalArgumentException("Details cannot be null");
        }
        this.origin = origin;
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(origin, that.origin) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, details);
    }
}
