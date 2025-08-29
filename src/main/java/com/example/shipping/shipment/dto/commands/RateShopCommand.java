package com.example.shipping.shipment.dto.commands;

import jakarta.validation.constraints.PositiveOrZero;

/** Optional knobs; keep it empty if you just want a trigger */
public record RateShopCommand(
        @PositiveOrZero Integer maxOptions,
        String preferredCarrier
) {}