package com.facundo.lumina.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateCompleteTransaction() {
        // Arrange
        SourceSystem source = new SourceSystem("BankA");
        TransactionDate date = new TransactionDate(LocalDate.now());
        TransactionOrigin origin = new TransactionOrigin(source, date);

        RawDescription raw = new RawDescription("Supermarket purchase");
        Category category = Category.GROCERIES;
        TransactionDescription description = new TransactionDescription(raw, category);

        Money money = new Money(new BigDecimal("50.00"), Currency.getInstance("EUR"));
        TransactionDetails details = new TransactionDetails(description, money);

        // Act
        Transaction transaction = new Transaction(origin, details);

        // Assert
        assertNotNull(transaction);
    }

    @Test
    void transactionsWithSameDataShouldBeEqual() {
        // Arrange
        LocalDate now = LocalDate.now();
        BigDecimal amount = new BigDecimal("10.00");
        Currency eur = Currency.getInstance("EUR");

        Transaction t1 = createTransaction("Source", now, "Desc", Category.UNKNOWN, amount, eur);
        Transaction t2 = createTransaction("Source", now, "Desc", Category.UNKNOWN, amount, eur);

        // Assert
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    private Transaction createTransaction(String src, LocalDate date, String desc, Category cat, BigDecimal amount, Currency curr) {
        return new Transaction(
            new TransactionOrigin(new SourceSystem(src), new TransactionDate(date)),
            new TransactionDetails(
                new TransactionDescription(new RawDescription(desc), cat),
                new Money(amount, curr)
            )
        );
    }
}
