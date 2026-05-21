package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.ErpEmployeeDTO;
import com.camtech.digitranscm.dto.ErpInvoiceDTO;
import com.camtech.digitranscm.dto.ErpSupplierDTO;
import com.camtech.digitranscm.entity.*;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErpServiceTest {

    @Mock private ErpEmployeeRepository employeeRepo;
    @Mock private ErpSupplierRepository supplierRepo;
    @Mock private ErpInvoiceRepository invoiceRepo;

    private ErpService service;

    @BeforeEach
    void setUp() {
        service = new ErpService(employeeRepo, supplierRepo, invoiceRepo);
    }

    @Test
    void getAllEmployees_returnsList() {
        when(employeeRepo.findAll()).thenReturn(List.of(new ErpEmployee()));
        assertThat(service.getAllEmployees()).hasSize(1);
    }

    @Test
    void getEmployeeById_found() {
        ErpEmployee emp = new ErpEmployee();
        emp.setId(1L);
        emp.setNom("Test");
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(emp));
        ErpEmployeeDTO dto = service.getEmployeeById(1L);
        assertThat(dto.getNom()).isEqualTo("Test");
    }

    @Test
    void getEmployeeById_notFound() {
        when(employeeRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createEmployee_saves() {
        ErpEmployeeDTO dto = new ErpEmployeeDTO();
        dto.setNom("Jean");
        dto.setPrenom("Dupont");
        dto.setEmail("jean@test.cm");
        dto.setDepartement("IT");
        dto.setPoste("Dev");
        dto.setDateEmbauche(LocalDate.now());

        ErpEmployee saved = new ErpEmployee();
        saved.setId(1L);
        saved.setNom("Jean");
        saved.setStatut("ACTIF");
        when(employeeRepo.save(any())).thenReturn(saved);

        ErpEmployeeDTO result = service.createEmployee(dto);
        assertThat(result.getNom()).isEqualTo("Jean");
        assertThat(result.getStatut()).isEqualTo("ACTIF");
    }

    @Test
    void deleteEmployee_exists() {
        when(employeeRepo.existsById(1L)).thenReturn(true);
        service.deleteEmployee(1L);
        verify(employeeRepo).deleteById(1L);
    }

    @Test
    void deleteEmployee_notFound() {
        when(employeeRepo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllSuppliers_returnsList() {
        when(supplierRepo.findAll()).thenReturn(List.of(new ErpSupplier()));
        assertThat(service.getAllSuppliers()).hasSize(1);
    }

    @Test
    void createSupplier_saves() {
        ErpSupplierDTO dto = new ErpSupplierDTO();
        dto.setNomSociete("Fournisseur SARL");
        dto.setEmail("contact@fournisseur.cm");

        ErpSupplier saved = new ErpSupplier();
        saved.setId(1L);
        saved.setNomSociete("Fournisseur SARL");
        saved.setStatut("ACTIF");
        when(supplierRepo.save(any())).thenReturn(saved);

        ErpSupplierDTO result = service.createSupplier(dto);
        assertThat(result.getNomSociete()).isEqualTo("Fournisseur SARL");
    }

    @Test
    void getAllInvoices_returnsList() {
        when(invoiceRepo.findAll()).thenReturn(List.of(new ErpInvoice()));
        assertThat(service.getAllInvoices()).hasSize(1);
    }

    @Test
    void createInvoice_saves() {
        ErpSupplier sup = new ErpSupplier();
        sup.setId(1L);
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(sup));

        ErpInvoiceDTO dto = new ErpInvoiceDTO();
        dto.setNumeroFacture("FAC-001");
        dto.setFournisseurId(1L);
        dto.setMontantHT(BigDecimal.valueOf(1000));

        ErpInvoice saved = new ErpInvoice();
        saved.setId(1L);
        saved.setNumeroFacture("FAC-001");
        saved.setFournisseur(sup);
        saved.setStatut("EN_ATTENTE");
        when(invoiceRepo.save(any())).thenReturn(saved);

        ErpInvoiceDTO result = service.createInvoice(dto);
        assertThat(result.getNumeroFacture()).isEqualTo("FAC-001");
    }
}
