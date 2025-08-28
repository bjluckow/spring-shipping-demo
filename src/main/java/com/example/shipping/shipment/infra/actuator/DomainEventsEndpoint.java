// shipment/infra/actuator/DomainEventsEndpoint.java
package com.example.shipping.shipment.infra.actuator;

import com.example.shipping.shipment.api.events.*;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

@Component
@Endpoint(id = "domainevents") // -> /actuator/domainevents
public class DomainEventsEndpoint {

  private final ArrayDeque<Map<String,Object>> ring = new ArrayDeque<>(200);

  private void record(String type, Map<String,Object> payload) {
    if (ring.size() == 200) ring.removeFirst();
    ring.addLast(Map.of("type", type, "at", Instant.now().toString(), "data", payload));
  }

  @ReadOperation
  public List<Map<String,Object>> read() { return List.copyOf(ring); }

  // Feed it from transactional listeners
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ShipmentCreated e) {
    record("ShipmentCreated", Map.of("shipmentId", e.shipmentId().toString()));
  }
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(LabelPurchased e) {
    record("LabelPurchased", Map.of("shipmentId", e.shipmentId().toString(), "tracking", e.trackingCode()));
  }
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ShipmentStatusChanged e) {
    record("ShipmentStatusChanged", Map.of("shipmentId", e.shipmentId().toString(), "newStatus", e.newStatus().name()));
  }
}