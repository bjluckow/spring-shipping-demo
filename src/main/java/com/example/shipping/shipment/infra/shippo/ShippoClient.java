package com.example.shipping.shipment.infra.shippo;

public class ShippoClient {
    private final String apiKey;
    public ShippoClient(String apiKey) { this.apiKey = apiKey; }

    public PurchasedLabel purchaseLabel(java.util.UUID shipmentId, String carrierService) {
        // TODO: invoke Shippo SDK; returning fake data for testing
        return new PurchasedLabel(carrierService, "https://labels.example/123", "TRACK123");
    }

    public record PurchasedLabel(String carrierService, String labelUrl, String trackingCode) {}
}
