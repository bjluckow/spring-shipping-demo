package com.example.shipping.shipment.api.queries;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetShipmentStatusQuery(@NotNull UUID shipmentId) {}