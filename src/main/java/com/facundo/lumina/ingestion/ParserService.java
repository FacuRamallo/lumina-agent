package com.facundo.lumina.ingestion;

import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParserService {

    private final Map<String, SourceParser> parsers;

    public ParserService(Map<String, SourceParser> parsers) {
        this.parsers = parsers;
    }

    public List<RawTransaction> parse(String sourceType, InputStream inputStream) {
        return Optional.ofNullable(parsers.get(sourceType.toUpperCase()))
                .map(parser -> parser.parse(inputStream))
                .orElseThrow(() -> new IllegalArgumentException("No parser found for type: " + sourceType));
    }
    
    public Map<String, SourceParser> getAvailableParsers() {
        return parsers;
    }
}
