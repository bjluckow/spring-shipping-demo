package com.example.shipping.shipment.infra.shippo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShippoConfig {
    @Bean ShippoClient shippoClient() { return new ShippoClient("fake-api-key"); }
}
