package com.facundo.lumina.infrastructure.config;

import com.facundo.lumina.domain.service.HashingService;
import com.facundo.lumina.domain.service.TransactionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TransactionMapper transactionMapper() {
        return new TransactionMapper();
    }

    @Bean
    public HashingService hashingService() {
        return new HashingService();
    }
}
