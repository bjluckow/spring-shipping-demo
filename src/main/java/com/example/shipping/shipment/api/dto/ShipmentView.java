package com.example.shipping.shipment.api.dto;

import com.example.shipping.shipment.domain.model.Status;

import java.util.UUID;

public record ShipmentView(UUID id, Status status, String labelUrl, String trackingCode) {}