package com.facundo.lumina.domain.service;

import com.facundo.lumina.domain.Category;
import com.facundo.lumina.domain.Transaction;
import com.facundo.lumina.domain.SourceSystem;
import com.facundo.lumina.infrastructure.ingestion.RawTransaction;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionMapperTest {

    @Test
    void shouldMapRawTransactionToDomainTransaction() {
        // Arrange
        RawTransaction raw = RawTransaction.builder()
                .date(LocalDate.of(2026, 5, 14))
                .description("Coffee shop")
                .amount(new BigDecimal("3.50"))
                .rawData("2026-05-14,Coffee shop,3.50")
                .build();
        
        SourceSystem source = new SourceSystem("TEST_SYSTEM");
        TransactionMapper mapper = new TransactionMapper();

        // Act
        Transaction transaction = mapper.map(raw, source, Category.GROCERIES);

        // Assert
        assertNotNull(transaction);
        // Equality is handled by deep equals in domain objects
        // We can verify by creating an expected transaction
    }
}
