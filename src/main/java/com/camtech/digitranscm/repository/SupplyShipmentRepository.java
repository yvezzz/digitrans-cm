package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.SupplyShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyShipmentRepository extends JpaRepository<SupplyShipment, Long> {
    List<SupplyShipment> findByStatut(String statut);
    List<SupplyShipment> findByOrigine(String origine);
    List<SupplyShipment> findByDestination(String destination);
    List<SupplyShipment> findByProduitId(Long produitId);
}
