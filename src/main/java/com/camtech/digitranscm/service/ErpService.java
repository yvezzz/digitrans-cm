package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.*;
import com.camtech.digitranscm.entity.*;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ErpService {

    private final ErpEmployeeRepository employeeRepo;
    private final ErpSupplierRepository supplierRepo;
    private final ErpInvoiceRepository invoiceRepo;

    public ErpService(ErpEmployeeRepository employeeRepo,
                      ErpSupplierRepository supplierRepo,
                      ErpInvoiceRepository invoiceRepo) {
        this.employeeRepo = employeeRepo;
        this.supplierRepo = supplierRepo;
        this.invoiceRepo = invoiceRepo;
    }

    // --- Employees ---
    public List<ErpEmployeeDTO> getAllEmployees() {
        return employeeRepo.findAll().stream()
                .map(this::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    public ErpEmployeeDTO getEmployeeById(Long id) {
        ErpEmployee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe introuvable avec l'id: " + id));
        return toEmployeeDTO(emp);
    }

    public ErpEmployeeDTO createEmployee(ErpEmployeeDTO dto) {
        ErpEmployee emp = new ErpEmployee();
        emp.setNom(dto.getNom());
        emp.setPrenom(dto.getPrenom());
        emp.setEmail(dto.getEmail());
        emp.setTelephone(dto.getTelephone());
        emp.setDepartement(dto.getDepartement());
        emp.setPoste(dto.getPoste());
        emp.setDateEmbauche(dto.getDateEmbauche());
        emp.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        emp.setAdresse(dto.getAdresse());
        ErpEmployee saved = employeeRepo.save(emp);
        return toEmployeeDTO(saved);
    }

    public ErpEmployeeDTO updateEmployee(Long id, ErpEmployeeDTO dto) {
        ErpEmployee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe introuvable avec l'id: " + id));
        emp.setNom(dto.getNom());
        emp.setPrenom(dto.getPrenom());
        emp.setEmail(dto.getEmail());
        emp.setTelephone(dto.getTelephone());
        emp.setDepartement(dto.getDepartement());
        emp.setPoste(dto.getPoste());
        emp.setDateEmbauche(dto.getDateEmbauche());
        emp.setAdresse(dto.getAdresse());
        if (dto.getStatut() != null) emp.setStatut(dto.getStatut());
        return toEmployeeDTO(employeeRepo.save(emp));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepo.existsById(id)) {
            throw new ResourceNotFoundException("Employe introuvable avec l'id: " + id);
        }
        employeeRepo.deleteById(id);
    }

    // --- Suppliers ---
    public List<ErpSupplierDTO> getAllSuppliers() {
        return supplierRepo.findAll().stream()
                .map(this::toSupplierDTO)
                .collect(Collectors.toList());
    }

    public ErpSupplierDTO getSupplierById(Long id) {
        ErpSupplier sup = supplierRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable avec l'id: " + id));
        return toSupplierDTO(sup);
    }

    public ErpSupplierDTO createSupplier(ErpSupplierDTO dto) {
        ErpSupplier sup = new ErpSupplier();
        sup.setNomSociete(dto.getNomSociete());
        sup.setContactPrincipal(dto.getContactPrincipal());
        sup.setTelephone(dto.getTelephone());
        sup.setEmail(dto.getEmail());
        sup.setAdresse(dto.getAdresse());
        sup.setCategorie(dto.getCategorie());
        sup.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        return toSupplierDTO(supplierRepo.save(sup));
    }

    public ErpSupplierDTO updateSupplier(Long id, ErpSupplierDTO dto) {
        ErpSupplier sup = supplierRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable avec l'id: " + id));
        sup.setNomSociete(dto.getNomSociete());
        sup.setContactPrincipal(dto.getContactPrincipal());
        sup.setTelephone(dto.getTelephone());
        sup.setEmail(dto.getEmail());
        sup.setAdresse(dto.getAdresse());
        sup.setCategorie(dto.getCategorie());
        if (dto.getStatut() != null) sup.setStatut(dto.getStatut());
        return toSupplierDTO(supplierRepo.save(sup));
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepo.existsById(id)) {
            throw new ResourceNotFoundException("Fournisseur introuvable avec l'id: " + id);
        }
        supplierRepo.deleteById(id);
    }

    // --- Invoices ---
    public List<ErpInvoiceDTO> getAllInvoices() {
        return invoiceRepo.findAll().stream()
                .map(this::toInvoiceDTO)
                .collect(Collectors.toList());
    }

    public ErpInvoiceDTO getInvoiceById(Long id) {
        ErpInvoice inv = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable avec l'id: " + id));
        return toInvoiceDTO(inv);
    }

    public ErpInvoiceDTO createInvoice(ErpInvoiceDTO dto) {
        ErpInvoice inv = new ErpInvoice();
        inv.setNumeroFacture(dto.getNumeroFacture());
        if (dto.getFournisseurId() != null) {
            ErpSupplier sup = supplierRepo.findById(dto.getFournisseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable"));
            inv.setFournisseur(sup);
        }
        inv.setDateEmission(dto.getDateEmission());
        inv.setDateEcheance(dto.getDateEcheance());
        inv.setMontantHT(dto.getMontantHT());
        inv.setMontantTVA(dto.getMontantTVA());
        inv.setMontantTTC(dto.getMontantTTC());
        inv.setStatut(dto.getStatut() != null ? dto.getStatut() : "EN_ATTENTE");
        inv.setNotes(dto.getNotes());
        return toInvoiceDTO(invoiceRepo.save(inv));
    }

    public ErpInvoiceDTO updateInvoice(Long id, ErpInvoiceDTO dto) {
        ErpInvoice inv = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable avec l'id: " + id));
        inv.setNumeroFacture(dto.getNumeroFacture());
        if (dto.getFournisseurId() != null) {
            ErpSupplier sup = supplierRepo.findById(dto.getFournisseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur introuvable"));
            inv.setFournisseur(sup);
        }
        inv.setDateEmission(dto.getDateEmission());
        inv.setDateEcheance(dto.getDateEcheance());
        inv.setMontantHT(dto.getMontantHT());
        inv.setMontantTVA(dto.getMontantTVA());
        inv.setMontantTTC(dto.getMontantTTC());
        inv.setNotes(dto.getNotes());
        if (dto.getStatut() != null) inv.setStatut(dto.getStatut());
        return toInvoiceDTO(invoiceRepo.save(inv));
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepo.existsById(id)) {
            throw new ResourceNotFoundException("Facture introuvable avec l'id: " + id);
        }
        invoiceRepo.deleteById(id);
    }

    // --- Mappers ---
    private ErpEmployeeDTO toEmployeeDTO(ErpEmployee emp) {
        ErpEmployeeDTO dto = new ErpEmployeeDTO();
        dto.setId(emp.getId());
        dto.setNom(emp.getNom());
        dto.setPrenom(emp.getPrenom());
        dto.setEmail(emp.getEmail());
        dto.setTelephone(emp.getTelephone());
        dto.setDepartement(emp.getDepartement());
        dto.setPoste(emp.getPoste());
        dto.setDateEmbauche(emp.getDateEmbauche());
        dto.setStatut(emp.getStatut());
        dto.setAdresse(emp.getAdresse());
        return dto;
    }

    private ErpSupplierDTO toSupplierDTO(ErpSupplier sup) {
        ErpSupplierDTO dto = new ErpSupplierDTO();
        dto.setId(sup.getId());
        dto.setNomSociete(sup.getNomSociete());
        dto.setContactPrincipal(sup.getContactPrincipal());
        dto.setTelephone(sup.getTelephone());
        dto.setEmail(sup.getEmail());
        dto.setAdresse(sup.getAdresse());
        dto.setCategorie(sup.getCategorie());
        dto.setStatut(sup.getStatut());
        return dto;
    }

    private ErpInvoiceDTO toInvoiceDTO(ErpInvoice inv) {
        ErpInvoiceDTO dto = new ErpInvoiceDTO();
        dto.setId(inv.getId());
        dto.setNumeroFacture(inv.getNumeroFacture());
        if (inv.getFournisseur() != null) {
            dto.setFournisseurId(inv.getFournisseur().getId());
            dto.setFournisseurNom(inv.getFournisseur().getNomSociete());
        }
        dto.setDateEmission(inv.getDateEmission());
        dto.setDateEcheance(inv.getDateEcheance());
        dto.setMontantHT(inv.getMontantHT());
        dto.setMontantTVA(inv.getMontantTVA());
        dto.setMontantTTC(inv.getMontantTTC());
        dto.setStatut(inv.getStatut());
        dto.setNotes(inv.getNotes());
        return dto;
    }
}
