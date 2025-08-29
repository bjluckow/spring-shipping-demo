package com.example.shipping.shipment.infra.persistence;

import com.example.shipping.shipment.domain.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {}
