package com.example.shipping.shipment.listeners;

import com.example.shipping.shipment.api.events.*;
import com.example.shipping.shipment.api.events.LabelPurchased;
import com.example.shipping.shipment.api.events.ShipmentCreated;
import com.example.shipping.shipment.api.events.ShipmentStatusChanged;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.util.Map;

@Component
public class ShipmentAuditBridge {
    private final AuditEventRepository audits;
    public ShipmentAuditBridge(AuditEventRepository audits) { this.audits = audits; }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentCreated e) {
        audits.add(new AuditEvent("system", "ShipmentCreated",
                Map.of("shipmentId", e.shipmentId().toString())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LabelPurchased e) {
        audits.add(new AuditEvent("system", "LabelPurchased",
                Map.of("shipmentId", e.shipmentId().toString(), "tracking", e.trackingCode())));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentStatusChanged e) {
        audits.add(new AuditEvent("system", "ShipmentStatusChanged",
                Map.of("shipmentId", e.shipmentId().toString(), "newStatus", e.newStatus().name())));
    }
}