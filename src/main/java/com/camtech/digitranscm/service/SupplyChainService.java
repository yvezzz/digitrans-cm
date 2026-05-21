package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.SupplyProductDTO;
import com.camtech.digitranscm.dto.SupplyShipmentDTO;
import com.camtech.digitranscm.entity.SupplyProduct;
import com.camtech.digitranscm.entity.SupplyShipment;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.SupplyProductRepository;
import com.camtech.digitranscm.repository.SupplyShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplyChainService {

    private final SupplyProductRepository productRepo;
    private final SupplyShipmentRepository shipmentRepo;

    public SupplyChainService(SupplyProductRepository productRepo,
                              SupplyShipmentRepository shipmentRepo) {
        this.productRepo = productRepo;
        this.shipmentRepo = shipmentRepo;
    }

    // --- Products ---
    public List<SupplyProductDTO> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    public SupplyProductDTO getProductById(Long id) {
        SupplyProduct prod = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id: " + id));
        return toProductDTO(prod);
    }

    public SupplyProductDTO createProduct(SupplyProductDTO dto) {
        SupplyProduct prod = new SupplyProduct();
        prod.setNom(dto.getNom());
        prod.setType(dto.getType());
        prod.setVariete(dto.getVariete());
        prod.setDescription(dto.getDescription());
        prod.setUniteMesure(dto.getUniteMesure() != null ? dto.getUniteMesure() : "KG");
        prod.setPrixUnitaire(dto.getPrixUnitaire());
        prod.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        return toProductDTO(productRepo.save(prod));
    }

    public SupplyProductDTO updateProduct(Long id, SupplyProductDTO dto) {
        SupplyProduct prod = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id: " + id));
        prod.setNom(dto.getNom());
        prod.setType(dto.getType());
        prod.setVariete(dto.getVariete());
        prod.setDescription(dto.getDescription());
        prod.setUniteMesure(dto.getUniteMesure());
        prod.setPrixUnitaire(dto.getPrixUnitaire());
        if (dto.getStatut() != null) prod.setStatut(dto.getStatut());
        return toProductDTO(productRepo.save(prod));
    }

    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("Produit introuvable avec l'id: " + id);
        }
        productRepo.deleteById(id);
    }

    // --- Shipments ---
    public List<SupplyShipmentDTO> getAllShipments() {
        return shipmentRepo.findAll().stream()
                .map(this::toShipmentDTO)
                .collect(Collectors.toList());
    }

    public SupplyShipmentDTO getShipmentById(Long id) {
        SupplyShipment ship = shipmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition introuvable avec l'id: " + id));
        return toShipmentDTO(ship);
    }

    public SupplyShipmentDTO createShipment(SupplyShipmentDTO dto) {
        SupplyShipment ship = new SupplyShipment();
        ship.setNumeroSuivi(dto.getNumeroSuivi());

        SupplyProduct prod = productRepo.findById(dto.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id: " + dto.getProduitId()));
        ship.setProduit(prod);

        ship.setQuantite(dto.getQuantite());
        ship.setOrigine(dto.getOrigine());
        ship.setDestination(dto.getDestination());
        ship.setStatut(dto.getStatut() != null ? dto.getStatut() : "EN_ATTENTE");
        ship.setNotes(dto.getNotes());
        ship.setDateExpedition(dto.getDateExpedition());
        ship.setDateLivraison(dto.getDateLivraison());
        return toShipmentDTO(shipmentRepo.save(ship));
    }

    public SupplyShipmentDTO updateShipment(Long id, SupplyShipmentDTO dto) {
        SupplyShipment ship = shipmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition introuvable avec l'id: " + id));
        ship.setNumeroSuivi(dto.getNumeroSuivi());

        if (dto.getProduitId() != null) {
            SupplyProduct prod = productRepo.findById(dto.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
            ship.setProduit(prod);
        }

        ship.setQuantite(dto.getQuantite());
        ship.setOrigine(dto.getOrigine());
        ship.setDestination(dto.getDestination());
        ship.setNotes(dto.getNotes());
        ship.setDateExpedition(dto.getDateExpedition());
        ship.setDateLivraison(dto.getDateLivraison());
        if (dto.getStatut() != null) ship.setStatut(dto.getStatut());
        return toShipmentDTO(shipmentRepo.save(ship));
    }

    public void deleteShipment(Long id) {
        if (!shipmentRepo.existsById(id)) {
            throw new ResourceNotFoundException("Expedition introuvable avec l'id: " + id);
        }
        shipmentRepo.deleteById(id);
    }

    // --- Mappers ---
    private SupplyProductDTO toProductDTO(SupplyProduct prod) {
        SupplyProductDTO dto = new SupplyProductDTO();
        dto.setId(prod.getId());
        dto.setNom(prod.getNom());
        dto.setType(prod.getType());
        dto.setVariete(prod.getVariete());
        dto.setDescription(prod.getDescription());
        dto.setUniteMesure(prod.getUniteMesure());
        dto.setPrixUnitaire(prod.getPrixUnitaire());
        dto.setStatut(prod.getStatut());
        return dto;
    }

    private SupplyShipmentDTO toShipmentDTO(SupplyShipment ship) {
        SupplyShipmentDTO dto = new SupplyShipmentDTO();
        dto.setId(ship.getId());
        dto.setNumeroSuivi(ship.getNumeroSuivi());
        if (ship.getProduit() != null) {
            dto.setProduitId(ship.getProduit().getId());
            dto.setProduitNom(ship.getProduit().getNom());
        }
        dto.setQuantite(ship.getQuantite());
        dto.setOrigine(ship.getOrigine());
        dto.setDestination(ship.getDestination());
        dto.setStatut(ship.getStatut());
        dto.setNotes(ship.getNotes());
        dto.setDateExpedition(ship.getDateExpedition());
        dto.setDateLivraison(ship.getDateLivraison());
        return dto;
    }
}
