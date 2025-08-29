package com.example.shipping.shipment.domain.listeners;


import com.example.shipping.shipment.domain.events.LabelPurchased;
import com.example.shipping.shipment.domain.events.ShipmentCreated;
import com.example.shipping.shipment.domain.events.ShipmentStatusChanged;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class ShipmentMetricsBridge {
    private final MeterRegistry meters;
    private final ObservationRegistry observations;

    public ShipmentMetricsBridge(MeterRegistry meters, ObservationRegistry observations) {
        this.meters = meters; this.observations = observations;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentCreated e) {
        meters.counter("shipment.events", "type", "created").increment();
        Observation.createNotStarted("shipment.event",
                        observations)
                .lowCardinalityKeyValue("type","created")
                .observe(() -> {}); // produces trace + metric if tracing enabled
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LabelPurchased e) {
        meters.counter("shipment.events", "type", "label_purchased").increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ShipmentStatusChanged e) {
        meters.counter("shipment.events", "type", "status_changed",
                "status", e.newStatus().name()).increment();
    }
}
