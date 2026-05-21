package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.ErpSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpSupplierRepository extends JpaRepository<ErpSupplier, Long> {
    List<ErpSupplier> findByStatut(String statut);
    List<ErpSupplier> findByCategorie(String categorie);
}
