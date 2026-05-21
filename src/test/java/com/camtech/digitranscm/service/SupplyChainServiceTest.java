package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.SupplyProductDTO;
import com.camtech.digitranscm.dto.SupplyShipmentDTO;
import com.camtech.digitranscm.entity.SupplyProduct;
import com.camtech.digitranscm.entity.SupplyShipment;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.SupplyProductRepository;
import com.camtech.digitranscm.repository.SupplyShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyChainServiceTest {

    @Mock private SupplyProductRepository productRepo;
    @Mock private SupplyShipmentRepository shipmentRepo;

    private SupplyChainService service;

    @BeforeEach
    void setUp() {
        service = new SupplyChainService(productRepo, shipmentRepo);
    }

    @Test
    void getAllProducts_returnsList() {
        when(productRepo.findAll()).thenReturn(List.of(new SupplyProduct()));
        assertThat(service.getAllProducts()).hasSize(1);
    }

    @Test
    void createProduct_saves() {
        SupplyProductDTO dto = new SupplyProductDTO();
        dto.setNom("Cacao bio");
        dto.setType("Matiere premiere");
        dto.setPrixUnitaire(BigDecimal.valueOf(2500));

        SupplyProduct saved = new SupplyProduct();
        saved.setId(1L);
        saved.setNom("Cacao bio");
        saved.setUniteMesure("KG");
        saved.setStatut("ACTIF");
        when(productRepo.save(any())).thenReturn(saved);

        assertThat(service.createProduct(dto).getNom()).isEqualTo("Cacao bio");
    }

    @Test
    void getProductById_notFound() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllShipments_returnsList() {
        when(shipmentRepo.findAll()).thenReturn(List.of(new SupplyShipment()));
        assertThat(service.getAllShipments()).hasSize(1);
    }

    @Test
    void createShipment_saves() {
        SupplyProduct prod = new SupplyProduct();
        prod.setId(1L);
        when(productRepo.findById(1L)).thenReturn(Optional.of(prod));

        SupplyShipmentDTO dto = new SupplyShipmentDTO();
        dto.setNumeroSuivi("SHP-001");
        dto.setProduitId(1L);
        dto.setQuantite(BigDecimal.valueOf(100));

        SupplyShipment saved = new SupplyShipment();
        saved.setId(1L);
        saved.setNumeroSuivi("SHP-001");
        saved.setProduit(prod);
        saved.setStatut("EN_ATTENTE");
        when(shipmentRepo.save(any())).thenReturn(saved);

        assertThat(service.createShipment(dto).getNumeroSuivi()).isEqualTo("SHP-001");
    }

    @Test
    void deleteShipment_notFound() {
        when(shipmentRepo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteShipment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
