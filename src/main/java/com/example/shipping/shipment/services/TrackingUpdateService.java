package com.example.shipping.shipment.services;

import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TrackingUpdateService {
    private final ShipmentRepository repo;
    public TrackingUpdateService(ShipmentRepository repo) { this.repo = repo; }

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
}