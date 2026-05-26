package com.facundo.lumina.domain.service;

import com.facundo.lumina.domain.DeduplicationId;
import com.facundo.lumina.domain.Transaction;
import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.domain.TransactionDate;
import com.facundo.lumina.domain.TransactionOrigin;
import com.facundo.lumina.domain.TransactionDescription;
import com.facundo.lumina.domain.RawDescription;
import com.facundo.lumina.domain.Category;
import com.facundo.lumina.domain.TransactionDetails;
import com.facundo.lumina.domain.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import static org.junit.jupiter.api.Assertions.*;

class HashingServiceTest {

    @Test
    void shouldGenerateSameHashForSameTransactionData() {
        // Arrange
        Transaction t1 = createTransaction("2026-05-14", "100.00", "Coffee");
        Transaction t2 = createTransaction("2026-05-14", "100.00", "Coffee");
        
        HashingService service = new HashingService();

        // Act
        DeduplicationId id1 = service.generateId(t1);
        DeduplicationId id2 = service.generateId(t2);

        // Assert
        assertEquals(id1, id2);
    }

    @Test
    void shouldGenerateDifferentHashForDifferentAmount() {
        Transaction t1 = createTransaction("2026-05-14", "100.00", "Coffee");
        Transaction t2 = createTransaction("2026-05-14", "100.01", "Coffee");
        
        HashingService service = new HashingService();

        assertNotEquals(service.generateId(t1), service.generateId(t2));
    }

    private Transaction createTransaction(String date, String amount, String desc) {
        return new Transaction(
            new TransactionOrigin(new SourceSystem("Bank"), new TransactionDate(LocalDate.parse(date))),
            new TransactionDetails(
                new TransactionDescription(new RawDescription(desc), Category.UNKNOWN),
                new Money(new BigDecimal(amount), Currency.getInstance("EUR"))
            )
        );
    }
}
