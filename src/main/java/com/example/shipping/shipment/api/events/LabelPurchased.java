package com.example.shipping.shipment.api.events;

import java.time.Instant;
import java.util.UUID;

public record LabelPurchased(UUID shipmentId, String carrierService, String trackingCode, Instant at) {}
