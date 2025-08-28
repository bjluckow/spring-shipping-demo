package com.example.shipping.shipment.services;


import com.example.shipping.shipment.api.commands.CreateShipmentCommand;
import com.example.shipping.shipment.domain.model.Shipment;
import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateShipmentService {
    private final ShipmentRepository repo;
    public CreateShipmentService(ShipmentRepository repo) { this.repo = repo; }

    @Transactional
    public UUID handle(CreateShipmentCommand c) {
        var id = UUID.randomUUID();
        var s = Shipment.create(
                id,
                new Shipment.Address(c.toName(), c.toLine1(), c.toCity(), c.toState(), c.toZip()),
                new Shipment.Dimensions(c.lengthCm(), c.widthCm(), c.heightCm()),
                c.weightGrams()
        );
        repo.save(s); // publishes ShipmentCreated after commit via @DomainEvents
        return id;
    }
}