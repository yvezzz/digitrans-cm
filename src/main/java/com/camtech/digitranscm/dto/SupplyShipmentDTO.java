package com.camtech.digitranscm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SupplyShipmentDTO {

    private Long id;

    @NotBlank(message = "Le numero de suivi est obligatoire")
    private String numeroSuivi;

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    private String produitNom;

    @NotNull(message = "La quantite est obligatoire")
    private BigDecimal quantite;

    @NotBlank(message = "L'origine est obligatoire")
    private String origine;

    @NotBlank(message = "La destination est obligatoire")
    private String destination;

    private String statut;
    private String notes;
    private LocalDateTime dateExpedition;
    private LocalDateTime dateLivraison;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }
    public Long getProduitId() { return produitId; }
    public void setProduitId(Long produitId) { this.produitId = produitId; }
    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public String getOrigine() { return origine; }
    public void setOrigine(String origine) { this.origine = origine; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDateExpedition() { return dateExpedition; }
    public void setDateExpedition(LocalDateTime dateExpedition) { this.dateExpedition = dateExpedition; }
    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
}
