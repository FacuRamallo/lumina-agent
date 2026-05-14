package com.facundo.lumina.domain;

import java.util.Objects;

public final class SourceSystem {
    private final String name;

    public SourceSystem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Source system name cannot be null or blank");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceSystem that = (SourceSystem) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
