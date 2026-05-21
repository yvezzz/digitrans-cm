package com.camtech.digitranscm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "erp_suppliers")
public class ErpSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nomSociete;

    @Column(length = 100)
    private String contactPrincipal;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(length = 50)
    private String categorie;

    @Column(length = 20)
    private String statut = "ACTIF";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }
    public String getContactPrincipal() { return contactPrincipal; }
    public void setContactPrincipal(String contactPrincipal) { this.contactPrincipal = contactPrincipal; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
