package com.camtech.digitranscm.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class SupplyProductDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String type;
    private String variete;
    private String description;
    private String uniteMesure;
    private BigDecimal prixUnitaire;
    private String statut;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getVariete() { return variete; }
    public void setVariete(String variete) { this.variete = variete; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUniteMesure() { return uniteMesure; }
    public void setUniteMesure(String uniteMesure) { this.uniteMesure = uniteMesure; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
