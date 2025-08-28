package com.example.shipping.shipment.api.commands;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import com.example.shipping.shipment.domain.model.Status;

/** For PATCH /api/shipments/{id} */
public record UpdateStatusCommand(@NotNull Status status, Instant at) {}