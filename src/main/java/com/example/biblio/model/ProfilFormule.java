package com.example.biblio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profil_formule")
public class ProfilFormule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profil;
    private int nombreDeMois;
    private int nblivrePort;

    public ProfilFormule() {}

    public ProfilFormule(Long id, String profil, int nombreDeMois, int nblivrePort) {
        this.id = id;
        this.profil = profil;
        this.nombreDeMois = nombreDeMois;
        this.nblivrePort = nblivrePort;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }

    public int getNombreDeMois() { return nombreDeMois; }
    public void setNombreDeMois(int nombreDeMois) { this.nombreDeMois = nombreDeMois; }

    public int getNblivrePort() { return nblivrePort; }
    public void setNblivrePort(int nblivrePort) { this.nblivrePort = nblivrePort; }
}