package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.CrmClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrmClientRepository extends JpaRepository<CrmClient, Long> {
    List<CrmClient> findByVille(String ville);
    List<CrmClient> findByStatut(String statut);
    List<CrmClient> findByCategorie(String categorie);
}
