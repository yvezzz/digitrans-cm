package com.camtech.digitranscm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrmRestaurantDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String enseigne;
    private String adresse;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    private String telephone;
    private String gerant;

    @NotNull(message = "La capacite est obligatoire")
    private Integer capacite;

    private String statut;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEnseigne() { return enseigne; }
    public void setEnseigne(String enseigne) { this.enseigne = enseigne; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getGerant() { return gerant; }
    public void setGerant(String gerant) { this.gerant = gerant; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
