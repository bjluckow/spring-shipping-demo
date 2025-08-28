package com.example.shipping.shipment.api.commands;

import jakarta.validation.constraints.NotBlank;

/** For POST /api/shipments/{id}/label */
public record PurchaseLabelCommand(@NotBlank String carrierService) {}
