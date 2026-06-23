package com.camtech.digitranscm.supplychain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String productId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String category;
    private String origin;
    private LocalDate harvestDate;
    private String status = "pending";
    private String blockchainHash;

    public Product() {
    }

    public Product(String productId, String name, String category, String origin, LocalDate harvestDate, String status, String blockchainHash) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.origin = origin;
        this.harvestDate = harvestDate;
        this.status = status;
        this.blockchainHash = blockchainHash;
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBlockchainHash() {
        return blockchainHash;
    }

    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }
}
