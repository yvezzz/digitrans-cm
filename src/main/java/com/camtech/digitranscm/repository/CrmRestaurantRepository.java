package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.CrmRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrmRestaurantRepository extends JpaRepository<CrmRestaurant, Long> {
    List<CrmRestaurant> findByVille(String ville);
    List<CrmRestaurant> findByStatut(String statut);
}
