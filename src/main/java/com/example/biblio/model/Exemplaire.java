package com.example.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exemplaire")
public class Exemplaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_exemplaire", nullable = false)
    private String numExemplaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "date_acquisition")
    private LocalDate dateAcquisition;

 

    @Column(name = "emplacement")
    private String emplacement;

    // Constructeurs
    public Exemplaire() {}

    public Exemplaire(Livre livre, String numExemplaire) {
        this.livre = livre;
        this.numExemplaire = numExemplaire;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public String getNumExemplaire() { return numExemplaire; }
    public void setNumExemplaire(String numExemplaire) { this.numExemplaire = numExemplaire; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }



    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
}