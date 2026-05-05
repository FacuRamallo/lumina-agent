package com.facundo.lumina.ingestion;

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

@Component("PLUXE")
public class PluxeCsvParser implements SourceParser {

    @Override
    public List<RawTransaction> parse(InputStream inputStream) {
        List<RawTransaction> transactions = new ArrayList<>();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(inputStream));
            // Skip 8 lines of preamble
            for (int i = 0; i < 8; i++) {
                reader.readLine();
            }

            try (CSVParser parser = new CSVParser(reader, 
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setAllowMissingColumnNames(true).build())) {
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                for (CSVRecord record : parser) {
                    if (record.get(0).trim().isEmpty() && record.size() > 1 && record.get(1).trim().isEmpty()) {
                        continue; // Skip empty trailing lines
                    }
                    transactions.add(RawTransaction.builder()
                            .date(LocalDate.parse(record.get("Fecha"), formatter))
                            .description(record.get("Descripción"))
                            .amount(new BigDecimal(record.get("Importe").replace("\"", "").replace(".", "").replace(",", ".")))
                            .rawData(record.toString())
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Pluxe CSV", e);
        }
        return transactions;
    }

    @Override
    public String getSourceType() {
        return "PLUXE";
    }
}
