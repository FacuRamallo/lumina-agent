package com.facundo.lumina.domain;

import java.util.Objects;

public final class DeduplicationId {
    private final String value;

    public DeduplicationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Deduplication ID cannot be null or blank");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeduplicationId that = (DeduplicationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
