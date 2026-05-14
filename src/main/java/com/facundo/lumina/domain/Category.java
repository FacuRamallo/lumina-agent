package com.facundo.lumina.domain;

import java.util.Objects;

public final class Category {
    private final String value;

    public Category(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(value, category.value);
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
