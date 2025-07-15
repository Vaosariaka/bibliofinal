package com.example.biblio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parametres_penalite")
public class ParametresPenalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_parametre", unique = true)
    private String nomParametre;

    @Column(name = "valeur")
    private Integer valeur;

    @Column(name = "description")
    private String description;

    public ParametresPenalite() {
    }

    public ParametresPenalite(String nomParametre, Integer valeur, String description) {
        this.nomParametre = nomParametre;
        this.valeur = valeur;
        this.description = description;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomParametre() {
        return nomParametre;
    }

    public void setNomParametre(String nomParametre) {
        this.nomParametre = nomParametre;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
