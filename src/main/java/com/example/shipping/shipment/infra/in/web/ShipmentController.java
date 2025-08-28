package com.example.shipping.shipment.infra.in.web;

import com.example.shipping.shipment.api.commands.CreateShipmentCommand;
import com.example.shipping.shipment.api.commands.PurchaseLabelCommand;
import com.example.shipping.shipment.api.commands.RateShopCommand;
import com.example.shipping.shipment.api.commands.UpdateStatusCommand;
import com.example.shipping.shipment.api.dto.ShipmentView;
import com.example.shipping.shipment.api.queries.GetShipmentStatusQuery;
import com.example.shipping.shipment.api.queries.ShipmentStatusView;
import com.example.shipping.shipment.domain.model.Shipment;
import com.example.shipping.shipment.infra.out.persistence.ShipmentRepository;
import com.example.shipping.shipment.services.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final CreateShipmentService create;
    private final UpdateStatusService update;
    private final PurchaseLabelService purchase;
    private final TrackingUpdateService tracking;
    private final ShipmentRepository repo;
    private final RateShopService rateShop;

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
        update.handle(id, cmd);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get shipment status")
    public ShipmentStatusView getStatus(@PathVariable UUID id) {
        var q = new GetShipmentStatusQuery(id);
        Shipment s = repo.findById(q.shipmentId()).orElseThrow();
        return new ShipmentStatusView(s.id(), s.status());
    }



    @io.swagger.v3.oas.annotations.Operation(summary = "Run rate shopping; transition to RATES_AVAILABLE")
    @PostMapping("/{id}/rates")
    public void rateShop(@PathVariable UUID id, @RequestBody(required = false) RateShopCommand cmd) {
        rateShop.handle(id, cmd == null ? new RateShopCommand(null, null) : cmd);
    }
}