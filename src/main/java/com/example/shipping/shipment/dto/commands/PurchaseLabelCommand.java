package com.example.shipping.shipment.dto.commands;

import jakarta.validation.constraints.NotBlank;

/** For POST /api/shipments/{id}/label */
public record PurchaseLabelCommand(@NotBlank String carrierService) {}
