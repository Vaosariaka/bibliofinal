package com.example.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalite")
public class Penalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emprunt_id")
    private Emprunt emprunt;

    @Column(name = "date_debut_penalite")
    private LocalDateTime dateDebutPenalite;

    @Column(name = "date_fin_penalite")
    private LocalDateTime dateFinPenalite;

    @Column(name = "motif")
    private String motif;

    @Column(name = "statut")
    private String statut; // ACTIVE, TERMINEE

    @Column(name = "jours_retard")
    private int joursRetard;

    @Column(name = "duree_penalite_jours")
    private int dureePenaliteJours;

    public Penalite() {
    }

    public Penalite(Users utilisateur, Emprunt emprunt, int joursRetard, int dureePenaliteJours, String motif) {
        this.utilisateur = utilisateur;
        this.emprunt = emprunt;
        this.joursRetard = joursRetard;
        this.dureePenaliteJours = dureePenaliteJours;
        this.motif = motif;
        this.dateDebutPenalite = LocalDateTime.now();
        this.dateFinPenalite = LocalDateTime.now().plusDays(dureePenaliteJours);
        this.statut = "ACTIVE";
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Users utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Emprunt getEmprunt() {
        return emprunt;
    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

    public LocalDateTime getDateDebutPenalite() {
        return dateDebutPenalite;
    }

    public void setDateDebutPenalite(LocalDateTime dateDebutPenalite) {
        this.dateDebutPenalite = dateDebutPenalite;
    }

    public LocalDateTime getDateFinPenalite() {
        return dateFinPenalite;
    }

    public void setDateFinPenalite(LocalDateTime dateFinPenalite) {
        this.dateFinPenalite = dateFinPenalite;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getJoursRetard() {
        return joursRetard;
    }

    public void setJoursRetard(int joursRetard) {
        this.joursRetard = joursRetard;
    }

    public int getDureePenaliteJours() {
        return dureePenaliteJours;
    }

    public void setDureePenaliteJours(int dureePenaliteJours) {
        this.dureePenaliteJours = dureePenaliteJours;
    }

    public boolean isActive() {
        return "ACTIVE".equals(statut) && dateFinPenalite.isAfter(LocalDateTime.now());
    }
}
