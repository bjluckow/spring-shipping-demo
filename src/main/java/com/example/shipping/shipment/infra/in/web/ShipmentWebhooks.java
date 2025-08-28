package com.example.shipping.shipment.infra.in.web;

import com.example.shipping.shipment.api.commands.RateShopCommand;
import com.example.shipping.shipment.api.commands.ShippoTrackingUpdateCommand;
import com.example.shipping.shipment.domain.model.Status;
import com.example.shipping.shipment.services.RateShopService;
import com.example.shipping.shipment.services.TrackingUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Receives Shippo webhooks and maps provider payloads to domain commands.
 * Secure with signature verification in production.
 */
@RestController
@RequestMapping("/webhooks/shippo")
@RequiredArgsConstructor
public class ShipmentWebhooks {

    private final TrackingUpdateService tracking;
    private final RateShopService rateShop;

     // Minimal payload you can expand to match Shippo's real schema
    public record ShippoEvent(@NotNull String event,
                              @NotNull Data data) {
        public record Data(@NotNull String tracking_number,
                           String carrier,
                           String status,   // e.g., "DELIVERED", "TRANSIT"
                           String status_date // ISO-8601
        ) {}
    }

    @Operation(summary = "Shippo webhook receiver")
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receive(@RequestBody @Valid ShippoEvent payload,
                        @RequestHeader Map<String, String> headers) {

        // (Optional) verify signature header here (HMAC) before processing.
        // See Shippo docs/portal for webhook setup & testing.
        // https://docs.goshippo.com/docs/tracking/webhooks/
        // Then normalize status:
        var normalized = normalizeStatus(payload.data().status());

        if (normalized == Status.IN_TRANSIT) {
            tracking.markInTransit(resolveShipmentId(payload), parseInstant(payload.data().status_date()));
        } else if (normalized == Status.DELIVERED) {
            tracking.markDelivered(resolveShipmentId(payload), parseInstant(payload.data().status_date()));
        }
        // ignore other statuses or map as needed (FAILED, RETURNED, etc.)
    }

    private UUID resolveShipmentId(ShippoEvent payload) {
        // Map tracking_number to our shipment ID (e.g., projection lookup)
        // For demo: assume tracking_number is actually our UUID
        return UUID.fromString(payload.data().tracking_number());
    }

    private Status normalizeStatus(String providerStatus) {
        if (providerStatus == null) return Status.IN_TRANSIT;
        return switch (providerStatus.toUpperCase()) {
            case "DELIVERED" -> Status.DELIVERED;
            case "TRANSIT", "IN_TRANSIT", "SHIPPING", "PRE_TRANSIT" -> Status.IN_TRANSIT;
            default -> Status.IN_TRANSIT;
        };
    }

    private Instant parseInstant(String s) {
        return (s == null || s.isBlank()) ? Instant.now() : Instant.parse(s);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Provider webhook: rates available")
    @PostMapping("/rates")
    @ResponseStatus(org.springframework.http.HttpStatus.ACCEPTED)
    public void ratesReady(@RequestBody Map<String, Object> payload) {
        // resolve shipmentId from payload
        var shipmentId = java.util.UUID.fromString(payload.get("shipment_id").toString());
        rateShop.handle(shipmentId, new RateShopCommand(null, null));
    }
}