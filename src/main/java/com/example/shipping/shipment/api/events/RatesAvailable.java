package com.example.shipping.shipment.api.events;

import java.time.Instant;
import java.util.UUID;

public record RatesAvailable(UUID shipmentId, Instant at) {}
