package com.example.shipping.shipment.api.events;

import java.time.Instant;
import java.util.UUID;

public record ShipmentCreated(UUID shipmentId, Instant at) {}
