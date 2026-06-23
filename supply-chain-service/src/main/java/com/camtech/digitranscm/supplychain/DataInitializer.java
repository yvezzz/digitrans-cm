package com.camtech.digitranscm.supplychain;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() == 0) {
            productRepository.save(new Product("PROD-001", "Cacao Premium", "Cacao", "Yaoundé", LocalDate.of(2026, 5, 15), "pending", ""));
            productRepository.save(new Product("PROD-002", "Café Arabica", "Café", "Bafoussam", LocalDate.of(2026, 5, 20), "in_transit", ""));
        }
    }
}
