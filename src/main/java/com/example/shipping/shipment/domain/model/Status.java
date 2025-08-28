package com.example.shipping.shipment.domain.model;

import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.EnumSet;


public enum Status {
    CREATED,
    RATES_AVAILABLE,
    LABEL_PURCHASED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    FAILED;

    private static final EnumMap<Status, EnumSet<Status>> ALLOWED =
            new EnumMap<>(Status.class);

    static {
        ALLOWED.put(CREATED, EnumSet.of(RATES_AVAILABLE, CANCELLED, FAILED));
        ALLOWED.put(RATES_AVAILABLE, EnumSet.of(LABEL_PURCHASED, CANCELLED, FAILED));
        ALLOWED.put(LABEL_PURCHASED, EnumSet.of(IN_TRANSIT, CANCELLED, FAILED));
        ALLOWED.put(IN_TRANSIT, EnumSet.of(DELIVERED, FAILED));
        ALLOWED.put(DELIVERED, EnumSet.noneOf(Status.class));
        ALLOWED.put(CANCELLED, EnumSet.noneOf(Status.class));
        ALLOWED.put(FAILED, EnumSet.of(CANCELLED));
    }

    public boolean canTransitionTo(Status target) {
        return ALLOWED.get(this).contains(target);
    }
}