package com.example.shipping.shipment.api.commands;

import jakarta.validation.constraints.*;
public record CreateShipmentCommand(
        @NotBlank String toName,
        @NotBlank String toLine1,
        @NotBlank String toCity,
        @NotBlank String toState,
        @NotBlank String toZip,
        @Positive int weightGrams,
        @Positive int lengthCm, @Positive int widthCm, @Positive int heightCm
) {}
