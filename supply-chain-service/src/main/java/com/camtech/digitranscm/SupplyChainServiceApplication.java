package com.camtech.digitranscm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupplyChainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainServiceApplication.class, args);
    }
}
