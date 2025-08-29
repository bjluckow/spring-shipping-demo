package com.example.shipping.shipment.domain.events;

import java.time.Instant;
import java.util.UUID;

public record RatesAvailable(UUID shipmentId, Instant at) {}
