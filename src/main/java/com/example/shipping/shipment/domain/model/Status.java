package com.example.shipping.shipment.domain.model;

public enum Status {
    CREATED, RATES_AVAILABLE, LABEL_PURCHASED, IN_TRANSIT, DELIVERED, CANCELLED, FAILED;

    public boolean canTransitionTo(Status target) {
        return switch (this) {
            case CREATED          -> target == RATES_AVAILABLE || target == CANCELLED || target == FAILED;
            case RATES_AVAILABLE  -> target == LABEL_PURCHASED || target == CANCELLED || target == FAILED;
            case LABEL_PURCHASED  -> target == IN_TRANSIT || target == CANCELLED || target == FAILED;
            case IN_TRANSIT       -> target == DELIVERED || target == FAILED;
            case DELIVERED        -> false;
            case CANCELLED        -> false;
            case FAILED           -> target == CANCELLED;
        };
    }
}