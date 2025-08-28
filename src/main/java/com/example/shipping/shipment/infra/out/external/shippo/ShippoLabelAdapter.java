package com.example.shipping.shipment.infra.out.external.shippo;


import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ShippoLabelAdapter {
    private final ShippoClient client;
    public ShippoLabelAdapter(ShippoClient client) { this.client = client; }

    public ShippoClient.PurchasedLabel purchase(UUID shipmentId, String carrierService) {
        return client.purchaseLabel(shipmentId, carrierService);
    }
}
