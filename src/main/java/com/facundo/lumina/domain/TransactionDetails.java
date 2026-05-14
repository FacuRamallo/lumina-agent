package com.facundo.lumina.domain;

import java.util.Objects;

public final class TransactionDetails {
    private final TransactionDescription description;
    private final Money money;

    public TransactionDetails(TransactionDescription description, Money money) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        if (money == null) {
            throw new IllegalArgumentException("Money cannot be null");
        }
        this.description = description;
        this.money = money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDetails that = (TransactionDetails) o;
        return Objects.equals(description, that.description) && Objects.equals(money, that.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, money);
    }

    @Override
    public String toString() {
        return description.toString() + "|" + money.toString();
    }
}
