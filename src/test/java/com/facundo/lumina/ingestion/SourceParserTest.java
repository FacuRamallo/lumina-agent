package com.facundo.lumina.ingestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SourceParserTest {

    private BankCsvParser bankParser;
    private PluxeCsvParser pluxeParser;

    @BeforeEach
    void setUp() {
        bankParser = new BankCsvParser();
        pluxeParser = new PluxeCsvParser();
    }

    @Test
    void shouldParseBankCsvCorrectly() {
        String csv = "Movimientos de la Cuenta,,  Número de cuenta:,1465 0120 3117 55005840,,,\n" +
                ",,  Titular:,FACUNDO RAMALLO MIRETTI,,,\n" +
                ",,  Fecha exportación:,04/05/2026 17:48h,,,\n" +
                "Fecha,Concepto,Importe\n01/05/2026,Starbucks coffee,\"-5,50\"";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        List<RawTransaction> transactions = bankParser.parse(is);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Starbucks coffee");
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo("-5.50");
    }

    @Test
    void shouldParsePluxeCsvCorrectly() {
        String csv = ",,,,,,,\n" +
                ",,,,,,,\n" +
                ",,,,,,Pluxee Tarjeta Restaurante,\n" +
                ",,,,,,**** **** ****,0657\n" +
                ",,,,,,,\n" +
                ",,,,,,\"174,29\",\n" +
                "HOJA DE MOVIMIENTOS,,2026-04-01 al 2026-04-30,,,,ACTIVA,\n" +
                ",,,,,,,\n" +
                "Fecha,Descripción,Importe,,,,,\n" +
                "28/04/2026,\"Carga de Adevinta Real Estate, SL\",\"231,00\",,,,,";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        List<RawTransaction> transactions = pluxeParser.parse(is);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Carga de Adevinta Real Estate, SL");
        assertThat(transactions.get(0).getAmount()).isEqualByComparingTo("231.00");
    }
}
