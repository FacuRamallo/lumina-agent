package com.facundo.lumina.infrastructure.ingestion;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component("BANK")
public class BankCsvParser implements SourceParser {

    @Override
    public List<RawTransaction> parse(InputStream inputStream) {
        List<RawTransaction> transactions = new ArrayList<>();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(inputStream));
            // Skip 3 lines of preamble
            for (int i = 0; i < 3; i++) {
                reader.readLine();
            }

            try (CSVParser parser = new CSVParser(reader, 
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
                
                for (CSVRecord record : parser) {
                    // Try to handle different variations of headers and data formats
                    String dateStr = record.isMapped("Fecha") ? record.get("Fecha") : record.get("Date");
                    String descStr = record.isMapped("Concepto") ? record.get("Concepto") : record.isMapped("Descripción") ? record.get("Descripción") : record.get("Description");
                    String amountStr = record.isMapped("Importe") ? record.get("Importe") : record.get("Amount");
                    
                    DateTimeFormatter formatter = dateStr.contains("/") ? DateTimeFormatter.ofPattern("dd/MM/yyyy") : DateTimeFormatter.ISO_LOCAL_DATE;
                    LocalDate date = LocalDate.parse(dateStr, formatter);
                    
                    BigDecimal amount = new BigDecimal(amountStr.replace(",", "."));
                    
                    transactions.add(RawTransaction.builder()
                            .date(date)
                            .description(descStr)
                            .amount(amount)
                            .rawData(record.toString())
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Bank CSV", e);
        }
        return transactions;
    }

    @Override
    public String getSourceType() {
        return "BANK";
    }
}
