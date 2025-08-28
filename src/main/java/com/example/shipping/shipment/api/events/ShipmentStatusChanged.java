package com.example.shipping.shipment.api.events;

import com.example.shipping.shipment.domain.model.Status;

import java.time.Instant;
import java.util.UUID;

public record ShipmentStatusChanged(UUID shipmentId, Status newStatus, Instant at, String reason) {
    public ShipmentStatusChanged(UUID shipmentId, Status newStatus, Instant timestamp) {
        this(shipmentId, newStatus, timestamp, null);
    }
}
