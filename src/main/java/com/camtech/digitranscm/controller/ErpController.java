package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.dto.ErpEmployeeDTO;
import com.camtech.digitranscm.dto.ErpInvoiceDTO;
import com.camtech.digitranscm.dto.ErpSupplierDTO;
import com.camtech.digitranscm.service.ErpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/erp")
@Tag(name = "ERP", description = "Gestion des ressources humaines, fournisseurs et factures")
public class ErpController {

    private final ErpService service;

    public ErpController(ErpService service) {
        this.service = service;
    }

    // --- Employees ---
    @GetMapping("/employees")
    @Operation(summary = "Liste tous les employes")
    public ResponseEntity<List<ErpEmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(service.getAllEmployees());
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "Recherche un employe par ID")
    public ResponseEntity<ErpEmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    @PostMapping("/employees")
    @Operation(summary = "Cree un nouvel employe")
    public ResponseEntity<ErpEmployeeDTO> createEmployee(@Valid @RequestBody ErpEmployeeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createEmployee(dto));
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Met a jour un employe")
    public ResponseEntity<ErpEmployeeDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody ErpEmployeeDTO dto) {
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Supprime un employe")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // --- Suppliers ---
    @GetMapping("/suppliers")
    @Operation(summary = "Liste tous les fournisseurs")
    public ResponseEntity<List<ErpSupplierDTO>> getAllSuppliers() {
        return ResponseEntity.ok(service.getAllSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    @Operation(summary = "Recherche un fournisseur par ID")
    public ResponseEntity<ErpSupplierDTO> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSupplierById(id));
    }

    @PostMapping("/suppliers")
    @Operation(summary = "Cree un nouveau fournisseur")
    public ResponseEntity<ErpSupplierDTO> createSupplier(@Valid @RequestBody ErpSupplierDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSupplier(dto));
    }

    @PutMapping("/suppliers/{id}")
    @Operation(summary = "Met a jour un fournisseur")
    public ResponseEntity<ErpSupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody ErpSupplierDTO dto) {
        return ResponseEntity.ok(service.updateSupplier(id, dto));
    }

    @DeleteMapping("/suppliers/{id}")
    @Operation(summary = "Supprime un fournisseur")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        service.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    // --- Invoices ---
    @GetMapping("/invoices")
    @Operation(summary = "Liste toutes les factures")
    public ResponseEntity<List<ErpInvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(service.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Recherche une facture par ID")
    public ResponseEntity<ErpInvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInvoiceById(id));
    }

    @PostMapping("/invoices")
    @Operation(summary = "Cree une nouvelle facture")
    public ResponseEntity<ErpInvoiceDTO> createInvoice(@Valid @RequestBody ErpInvoiceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInvoice(dto));
    }

    @PutMapping("/invoices/{id}")
    @Operation(summary = "Met a jour une facture")
    public ResponseEntity<ErpInvoiceDTO> updateInvoice(@PathVariable Long id, @Valid @RequestBody ErpInvoiceDTO dto) {
        return ResponseEntity.ok(service.updateInvoice(id, dto));
    }

    @DeleteMapping("/invoices/{id}")
    @Operation(summary = "Supprime une facture")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        service.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
