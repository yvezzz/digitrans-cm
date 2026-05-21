package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.SupplyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyProductRepository extends JpaRepository<SupplyProduct, Long> {
    List<SupplyProduct> findByType(String type);
    List<SupplyProduct> findByStatut(String statut);
}
