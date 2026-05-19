package com.facundo.lumina.domain.service;

import com.facundo.lumina.domain.DeduplicationId;
import com.facundo.lumina.domain.Transaction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashingService {

    public DeduplicationId generateId(Transaction transaction) {
        String dataToHash = transaction.toString(); // We'll ensure toString is stable and contains required fields
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            return new DeduplicationId(HexFormat.of().formatHex(encodedHash));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
