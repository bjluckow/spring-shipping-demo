package com.example.shipping.shipment.infra.in.web;

import com.example.shipping.shipment.api.commands.CreateShipmentCommand;
import com.example.shipping.shipment.api.commands.PurchaseLabelCommand;
import com.example.shipping.shipment.api.commands.UpdateStatusCommand;
import com.example.shipping.shipment.api.dto.ShipmentView;
import com.example.shipping.shipment.api.queries.GetShipmentStatusQuery;
import com.example.shipping.shipment.api.queries.ShipmentStatusView;
import com.example.shipping.shipment.domain.model.Shipment;
import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import com.example.shipping.shipment.services.CreateShipmentService;
import com.example.shipping.shipment.services.PurchaseLabelService;
import com.example.shipping.shipment.services.TrackingUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final CreateShipmentService create;
    private final PurchaseLabelService purchase;
    private final TrackingUpdateService tracking;
    private final ShipmentRepository repo;

    public ShipmentController(CreateShipmentService create, PurchaseLabelService purchase,
                              TrackingUpdateService tracking, ShipmentRepository repo) {
        this.create = create; this.purchase = purchase; this.tracking = tracking; this.repo = repo;
    }

    @Operation(summary = "Create shipment")
    @PostMapping
    public UUID create(@RequestBody @Valid CreateShipmentCommand cmd) {
        return create.handle(cmd);
    }

    @Operation(summary = "Get shipment")
    @GetMapping("/{id}")
    public ShipmentView get(@PathVariable UUID id) {
        Shipment s = repo.findById(id).orElseThrow();
        // adapt to your getters (Optional or String)
        return new ShipmentView(s.id(), s.status(),
                s.getLabelUrl(), s.getTrackingCode());
    }

    @Operation(summary = "Purchase label")
    @PostMapping("/{id}/label")
    public void purchase(@PathVariable UUID id, @RequestBody @Valid PurchaseLabelCommand cmd) {
        purchase.handle(id, cmd.carrierService());
    }

    @Operation(summary = "Partially update shipment (status, etc.)")
    @PatchMapping("/{id}")
    public void patch(@PathVariable UUID id, @RequestBody @Valid UpdateStatusCommand cmd) {
        // Monotonic timestamp if provided; fallback to now
        Instant at = cmd.at() != null ? cmd.at() : Instant.now();
        switch (cmd.status()) {
            case IN_TRANSIT -> tracking.markInTransit(id, at);
            case DELIVERED  -> tracking.markDelivered(id, at);
            case CANCELLED  -> tracking.cancel(id, "manual"); // if you expose this in your service
            default -> throw new IllegalArgumentException("Unsupported status via PATCH: " + cmd.status());
        }
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get shipment status")
    public ShipmentStatusView getStatus(@PathVariable UUID id) {
        var q = new GetShipmentStatusQuery(id);
        Shipment s = repo.findById(q.shipmentId()).orElseThrow();
        return new ShipmentStatusView(s.id(), s.status());
    }
}