package com.facundo.lumina.application;

import com.facundo.lumina.domain.DeduplicationId;
import com.facundo.lumina.domain.Transaction;

public record ProcessedTransaction(Transaction transaction, DeduplicationId id) {
}
