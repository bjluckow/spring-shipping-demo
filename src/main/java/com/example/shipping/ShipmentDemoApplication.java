package com.example.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;


@SpringBootApplication
@Modulithic
public class ShipmentDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShipmentDemoApplication.class, args);
    }
}
