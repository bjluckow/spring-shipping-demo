package com.example.shipping.shipment.listeners;

import com.example.shipping.shipment.api.events.*;
import com.example.shipping.shipment.api.events.LabelPurchased;
import com.example.shipping.shipment.api.events.ShipmentCreated;
import com.example.shipping.shipment.api.events.ShipmentStatusChanged;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class ShipmentEventHandlers {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentCreated e) {
        // e.g., log or create a projection row
        System.out.println("[EVENT] ShipmentCreated " + e.shipmentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LabelPurchased e) {
        System.out.println("[EVENT] LabelPurchased " + e.trackingCode());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentStatusChanged e) {
        System.out.println("[EVENT] StatusChanged -> " + e.newStatus());
    }
}