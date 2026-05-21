package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.ErpInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ErpInvoiceRepository extends JpaRepository<ErpInvoice, Long> {
    List<ErpInvoice> findByStatut(String statut);
    List<ErpInvoice> findByDateEmissionBetween(LocalDate debut, LocalDate fin);
    List<ErpInvoice> findByFournisseurId(Long fournisseurId);
}
