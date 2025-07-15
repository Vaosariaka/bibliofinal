package com.example.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livre_id")
    private Livre livre;

    @Column(name = "date_reservation")
    private LocalDate dateReservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exemplaire_attribue")
    private Exemplaire exemplaireAttribue;

    @Column(name = "statut")
    private String statut; // ACTIVE, ANNULEE

    public Reservation() {
    }

    public Reservation(Users utilisateur, Livre livre) {
        this.utilisateur = utilisateur;
        this.livre = livre;
        this.dateReservation = LocalDate.now();
        this.statut = "ACTIVE";
        this.exemplaireAttribue = null; // Pas d'exemplaire attribué au moment de la réservation
    }

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

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Exemplaire getExemplaireAttribue() {
        return exemplaireAttribue;
    }

    public void setExemplaireAttribue(Exemplaire exemplaireAttribue) {
        this.exemplaireAttribue = exemplaireAttribue;
    }
}
