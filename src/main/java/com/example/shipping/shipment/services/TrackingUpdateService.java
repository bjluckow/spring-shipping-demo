package com.example.shipping.shipment.services;

import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingUpdateService {
    private final ShipmentRepository repo;

    @Transactional
    public void markInTransit(UUID id, Instant at) {
        var s = repo.findById(id).orElseThrow();
        s.markInTransit(at);
        repo.save(s);
    }

    @Transactional
    public void markDelivered(UUID id, Instant at) {
        var s = repo.findById(id).orElseThrow();
        s.markDelivered(at);
        repo.save(s);
    }

    @Transactional
    public void cancel(UUID id, String reason) {
        var s = repo.findById(id).orElseThrow();
        s.cancel(reason);
        repo.save(s);
    }
}