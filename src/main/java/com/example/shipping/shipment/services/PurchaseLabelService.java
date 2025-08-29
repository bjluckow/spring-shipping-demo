package com.example.shipping.shipment.services;

import com.example.shipping.shipment.infra.shippo.ShippoLabelAdapter;
import com.example.shipping.shipment.infra.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseLabelService {
    private final ShipmentRepository repo;
    private final ShippoLabelAdapter shippo;

    @Transactional
    public void handle(UUID shipmentId, String carrierService) {
        var s = repo.findById(shipmentId).orElseThrow();
        var label = shippo.purchase(shipmentId, carrierService); // calls SDK
        s.labelPurchased(label.carrierService(), label.labelUrl(), label.trackingCode());
        repo.save(s); // publishes LabelPurchased after commit
    }
}
