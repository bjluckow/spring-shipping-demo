package com.example.shipping.shipment.domain.events;

import java.time.Instant;
import java.util.UUID;

public record LabelPurchased(UUID shipmentId, String carrierService, String trackingCode, Instant at) {}
