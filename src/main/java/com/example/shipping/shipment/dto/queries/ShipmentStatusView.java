package com.example.shipping.shipment.dto.queries;

import com.example.shipping.shipment.domain.model.Status;
import java.util.UUID;

public record ShipmentStatusView(UUID shipmentId, Status status) {}