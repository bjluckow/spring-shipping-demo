package com.example.shipping.shipment.services;

import com.example.shipping.shipment.infra.shippo.ShippoLabelAdapter;
import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PurchaseLabelService {
    private final ShipmentRepository repo;
    private final ShippoLabelAdapter shippo;

    public PurchaseLabelService(ShipmentRepository repo, ShippoLabelAdapter shippo) {
        this.repo = repo; this.shippo = shippo;
    }

    @Transactional
    public void handle(UUID shipmentId, String carrierService) {
        var s = repo.findById(shipmentId).orElseThrow();
        var label = shippo.purchase(shipmentId, carrierService); // calls SDK
        s.labelPurchased(label.carrierService(), label.labelUrl(), label.trackingCode());
        repo.save(s); // publishes LabelPurchased after commit
    }
}
