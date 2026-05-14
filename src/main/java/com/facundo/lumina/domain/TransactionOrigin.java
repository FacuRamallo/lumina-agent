package com.facundo.lumina.domain;

import java.util.Objects;

public final class TransactionOrigin {
    private final SourceSystem source;
    private final TransactionDate date;

    public TransactionOrigin(SourceSystem source, TransactionDate date) {
        if (source == null) {
            throw new IllegalArgumentException("Source system cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.source = source;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionOrigin that = (TransactionOrigin) o;
        return Objects.equals(source, that.source) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, date);
    }

    @Override
    public String toString() {
        return date.toString();
    }
}
