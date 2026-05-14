package com.facundo.lumina.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithAmountAndCurrency() {
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.getInstance("EUR");
        Money money = new Money(amount, currency);
        
        assertNotNull(money);
    }

    @Test
    void shouldNotAllowNullAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, Currency.getInstance("EUR")));
    }

    @Test
    void shouldNotAllowNullCurrency() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("100.00"), null));
    }
}
