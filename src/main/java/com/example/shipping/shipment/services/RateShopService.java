package com.example.shipping.shipment.services;


import com.example.shipping.shipment.dto.commands.RateShopCommand;
import com.example.shipping.shipment.infra.persistence.ShipmentRepository;
import com.example.shipping.shipment.domain.model.Shipment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class RateShopService {
    private final ShipmentRepository repo;

    public RateShopService(ShipmentRepository repo) { this.repo = repo; }

    @Transactional
    public void handle(UUID shipmentId, RateShopCommand cmd) {
        Shipment s = repo.findById(shipmentId).orElseThrow();
        // (Optional) call an outbound RateShopGateway here and stash options on the aggregate
        s.ratesReceived();                 // <-- transitions + emits RatesAvailable event
        repo.save(s);                      // publishes @DomainEvents on save
    }
}