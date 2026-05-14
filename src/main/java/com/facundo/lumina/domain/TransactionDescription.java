package com.facundo.lumina.domain;

import java.util.Objects;

public final class TransactionDescription {
    private final RawDescription raw;
    private final Category category;

    public TransactionDescription(RawDescription raw, Category category) {
        if (raw == null) {
            throw new IllegalArgumentException("Raw description cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.raw = raw;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDescription that = (TransactionDescription) o;
        return Objects.equals(raw, that.raw) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raw, category);
    }

    @Override
    public String toString() {
        return raw.toString();
    }
}
