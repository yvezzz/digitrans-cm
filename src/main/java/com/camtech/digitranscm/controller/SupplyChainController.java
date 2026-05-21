package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.dto.SupplyProductDTO;
import com.camtech.digitranscm.dto.SupplyShipmentDTO;
import com.camtech.digitranscm.service.SupplyChainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supply")
@Tag(name = "Supply Chain", description = "Gestion des produits et expeditions")
public class SupplyChainController {

    private final SupplyChainService service;

    public SupplyChainController(SupplyChainService service) {
        this.service = service;
    }

    @GetMapping("/products")
    @Operation(summary = "Liste tous les produits")
    public ResponseEntity<List<SupplyProductDTO>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Recherche un produit par ID")
    public ResponseEntity<SupplyProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductById(id));
    }

    @PostMapping("/products")
    @Operation(summary = "Cree un nouveau produit")
    public ResponseEntity<SupplyProductDTO> createProduct(@Valid @RequestBody SupplyProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createProduct(dto));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Met a jour un produit")
    public ResponseEntity<SupplyProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody SupplyProductDTO dto) {
        return ResponseEntity.ok(service.updateProduct(id, dto));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Supprime un produit")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shipments")
    @Operation(summary = "Liste toutes les expeditions")
    public ResponseEntity<List<SupplyShipmentDTO>> getAllShipments() {
        return ResponseEntity.ok(service.getAllShipments());
    }

    @GetMapping("/shipments/{id}")
    @Operation(summary = "Recherche une expedition par ID")
    public ResponseEntity<SupplyShipmentDTO> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getShipmentById(id));
    }

    @PostMapping("/shipments")
    @Operation(summary = "Cree une nouvelle expedition")
    public ResponseEntity<SupplyShipmentDTO> createShipment(@Valid @RequestBody SupplyShipmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createShipment(dto));
    }

    @PutMapping("/shipments/{id}")
    @Operation(summary = "Met a jour une expedition")
    public ResponseEntity<SupplyShipmentDTO> updateShipment(@PathVariable Long id, @Valid @RequestBody SupplyShipmentDTO dto) {
        return ResponseEntity.ok(service.updateShipment(id, dto));
    }

    @DeleteMapping("/shipments/{id}")
    @Operation(summary = "Supprime une expedition")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        service.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}
