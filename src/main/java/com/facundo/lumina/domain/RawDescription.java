package com.facundo.lumina.domain;

import java.util.Objects;

public final class RawDescription {
    private final String value;

    public RawDescription(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawDescription that = (RawDescription) o;
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
