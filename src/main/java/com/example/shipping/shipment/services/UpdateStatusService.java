package com.example.shipping.shipment.services;

import com.example.shipping.shipment.api.commands.UpdateStatusCommand;
import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateStatusService {
    private final ShipmentRepository repo;

    @Transactional
    public void handle(UUID id, UpdateStatusCommand cmd) {
        var s = repo.findById(id).orElseThrow();
        var at = cmd.at() != null ? cmd.at() : Instant.now();

        switch (cmd.status()) {
            case RATES_AVAILABLE -> s.ratesReceived();
            case IN_TRANSIT      -> s.markInTransit(at);
            case DELIVERED       -> s.markDelivered(at);
            case CANCELLED       -> s.cancel("manual");
            default -> throw new IllegalArgumentException("Unsupported: " + cmd.status());
        }
        repo.save(s); // <-- required for @DomainEvents publish
    }
}
