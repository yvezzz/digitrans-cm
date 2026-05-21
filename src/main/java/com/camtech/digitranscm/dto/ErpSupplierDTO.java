package com.camtech.digitranscm.dto;

import jakarta.validation.constraints.NotBlank;

public class ErpSupplierDTO {

    private Long id;

    @NotBlank(message = "Le nom de la societe est obligatoire")
    private String nomSociete;

    private String contactPrincipal;
    private String telephone;
    private String email;
    private String adresse;
    private String categorie;
    private String statut;

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
}
