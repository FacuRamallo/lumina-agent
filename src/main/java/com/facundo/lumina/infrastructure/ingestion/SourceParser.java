package com.facundo.lumina.infrastructure.ingestion;

import java.io.InputStream;
import java.util.List;

public interface SourceParser {
    /**
     * Parses the given input stream into a list of RawTransactions.
     * @param inputStream The source data stream.
     * @return List of parsed transactions.
     */
    List<RawTransaction> parse(InputStream inputStream);

    /**
     * Returns the source type this parser handles (e.g., "BANK", "PLUXE").
     */
    String getSourceType();
}
