package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.dto.CrmClientDTO;
import com.camtech.digitranscm.dto.CrmRestaurantDTO;
import com.camtech.digitranscm.service.CrmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crm")
@Tag(name = "CRM", description = "Gestion des clients et restaurants SavoirManger")
public class CrmController {

    private final CrmService service;

    public CrmController(CrmService service) {
        this.service = service;
    }

    @GetMapping("/clients")
    @Operation(summary = "Liste tous les clients")
    public ResponseEntity<List<CrmClientDTO>> getAllClients() {
        return ResponseEntity.ok(service.getAllClients());
    }

    @GetMapping("/clients/{id}")
    @Operation(summary = "Recherche un client par ID")
    public ResponseEntity<CrmClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getClientById(id));
    }

    @PostMapping("/clients")
    @Operation(summary = "Cree un nouveau client")
    public ResponseEntity<CrmClientDTO> createClient(@Valid @RequestBody CrmClientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createClient(dto));
    }

    @PutMapping("/clients/{id}")
    @Operation(summary = "Met a jour un client")
    public ResponseEntity<CrmClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody CrmClientDTO dto) {
        return ResponseEntity.ok(service.updateClient(id, dto));
    }

    @DeleteMapping("/clients/{id}")
    @Operation(summary = "Supprime un client")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        service.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurants")
    @Operation(summary = "Liste tous les restaurants")
    public ResponseEntity<List<CrmRestaurantDTO>> getAllRestaurants() {
        return ResponseEntity.ok(service.getAllRestaurants());
    }

    @GetMapping("/restaurants/{id}")
    @Operation(summary = "Recherche un restaurant par ID")
    public ResponseEntity<CrmRestaurantDTO> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRestaurantById(id));
    }

    @PostMapping("/restaurants")
    @Operation(summary = "Cree un nouveau restaurant")
    public ResponseEntity<CrmRestaurantDTO> createRestaurant(@Valid @RequestBody CrmRestaurantDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRestaurant(dto));
    }

    @PutMapping("/restaurants/{id}")
    @Operation(summary = "Met a jour un restaurant")
    public ResponseEntity<CrmRestaurantDTO> updateRestaurant(@PathVariable Long id, @Valid @RequestBody CrmRestaurantDTO dto) {
        return ResponseEntity.ok(service.updateRestaurant(id, dto));
    }

    @DeleteMapping("/restaurants/{id}")
    @Operation(summary = "Supprime un restaurant")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        service.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
