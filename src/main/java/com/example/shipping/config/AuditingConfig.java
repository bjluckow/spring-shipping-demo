package com.example.shipping.config;


import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditingConfig {

    // For development/testing. Consider a persistent impl for production.
    @Bean
    AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository(); // stores recent events in-memory
    }
}
