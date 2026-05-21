package com.camtech.digitranscm.repository;

import com.camtech.digitranscm.entity.ErpEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpEmployeeRepository extends JpaRepository<ErpEmployee, Long> {
    List<ErpEmployee> findByDepartement(String departement);
    List<ErpEmployee> findByStatut(String statut);
    boolean existsByEmail(String email);
}
