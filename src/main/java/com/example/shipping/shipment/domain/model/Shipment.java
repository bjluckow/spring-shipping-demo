package com.example.shipping.shipment.domain.model;

import com.example.shipping.shipment.api.events.LabelPurchased;
import com.example.shipping.shipment.api.events.RatesAvailable;
import com.example.shipping.shipment.api.events.ShipmentCreated;
import com.example.shipping.shipment.api.events.ShipmentStatusChanged;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "shipments")
@Getter
@Setter
public class Shipment {

    @Id @Column(nullable = false, updatable = false)
    private UUID id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column private String toName;
    @Column private String toLine1;
    @Column private String toCity;
    @Column private String toState;
    @Column private String toZip;
    @Column private Integer weightGrams;
    @Column private Integer lengthCm;
    @Column private Integer widthCm;
    @Column private Integer heightCm;

    @Column private String carrierService;
    @Column private String labelUrl;
    @Column private String trackingCode;
    @Column private Instant lastCheckpointAt;

    @Transient
    private final List<Object> events = new ArrayList<>();

    protected Shipment() {}

    private Shipment(UUID id, Address to, Dimensions dims, int weightGrams) {
        this.id = Objects.requireNonNull(id);
        this.status = Status.CREATED;
        this.toName = to.name(); this.toLine1 = to.line1();
        this.toCity = to.city(); this.toState = to.state(); this.toZip = to.zip();
        this.lengthCm = dims.lengthCm(); this.widthCm = dims.widthCm(); this.heightCm = dims.heightCm();
        this.weightGrams = weightGrams;
        emit(new ShipmentCreated(id, Instant.now()));
    }

    public static Shipment create(UUID id, Address to, Dimensions dims, int weightGrams) {
        return new Shipment(id, to, dims, weightGrams);
    }

    // Commands (idempotent + invariant-checked)
    public void ratesReceived() {
        transitionTo(Status.RATES_AVAILABLE);
    }

//    public void labelPurchased(String carrierService, String labelUrl, String trackingCode) {
//        this.carrierService = carrierService; this.labelUrl = labelUrl; this.trackingCode = trackingCode;
//        transitionTo(Status.LABEL_PURCHASED);
//        emit(new LabelPurchased(id, carrierService, trackingCode, Instant.now()));
//    }

    public void labelPurchased(String carrierService, String labelUrl, String trackingCode) {
        // 1) validate without mutating
        if (!status.canTransitionTo(Status.LABEL_PURCHASED)) {
            throw new InvalidTransitionException("Cannot transition " + status + " -> LABEL_PURCHASED");
        }

        // 2) enqueue specific event FIRST
        emit(new LabelPurchased(id, carrierService, trackingCode, Instant.now()));

        // 3) apply state + enqueue generic status change (here or inside transitionTo)
        this.carrierService = carrierService;
        this.labelUrl = labelUrl;
        this.trackingCode = trackingCode;

        transitionTo(Status.LABEL_PURCHASED);                 // if this emits a generic event
        // OR, if transitionTo() does not emit, do it explicitly:
        // emit(new ShipmentStatusChanged(id, Status.LABEL_PURCHASED, Instant.now()));
    }

    public void markInTransit(Instant checkpointTime) {
        if (isStale(checkpointTime)) return;
        this.lastCheckpointAt = checkpointTime;
        transitionTo(Status.IN_TRANSIT);
    }

    public void markDelivered(Instant checkpointTime) {
        if (isStale(checkpointTime)) return;
        this.lastCheckpointAt = checkpointTime;
        transitionTo(Status.DELIVERED);
    }

    public void cancel(String reason) {
        transitionTo(Status.CANCELLED, reason);
    }

    // Helpers
    private boolean isStale(Instant checkpoint) {
        return lastCheckpointAt != null && checkpoint != null && checkpoint.isBefore(lastCheckpointAt);
    }

    private void transitionTo(Status newStatus) {
        transitionTo(newStatus, null);
    }

    private void transitionTo(Status target, String reason) {
        if (status == target) return; // idempotent duplicate
        if (!status.canTransitionTo(target)) {
            throw new InvalidTransitionException("Cannot transition " + status + " -> " + target);
        }
        this.status = target;
        emit(new ShipmentStatusChanged(id, target, Instant.now(), reason)); // <— generic event
    }

    private void emit(Object event) { events.add(event); }

    // Domain events (Spring Data publishes on save)
    @DomainEvents public Collection<Object> domainEvents() { return List.copyOf(events); }
    @AfterDomainEventPublication public void clearDomainEvents() { events.clear(); }

    // Value objects
    public record Address(String name, String line1, String city, String state, String zip) {}
    public record Dimensions(int lengthCm, int widthCm, int heightCm) {}

    public UUID id() { return id; }
    public Status status() { return status; }

    public static final class InvalidTransitionException extends RuntimeException {
        public InvalidTransitionException(String message) { super(message); }
    }
}