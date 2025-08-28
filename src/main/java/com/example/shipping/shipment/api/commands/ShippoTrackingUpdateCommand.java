package com.example.shipping.shipment.api.commands;


import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import com.example.shipping.shipment.domain.model.Status;

/** Internal command both webhook + manual paths map to */
public record ShippoTrackingUpdateCommand(@NotNull UUID shipmentId,
                                          @NotNull Status status,
                                          Instant checkpointAt) {}