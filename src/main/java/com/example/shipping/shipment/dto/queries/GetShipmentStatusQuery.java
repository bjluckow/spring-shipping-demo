package com.example.shipping.shipment.dto.queries;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetShipmentStatusQuery(@NotNull UUID shipmentId) {}