package com.example.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emprunt")
public class Emprunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exemplaire_id", nullable = false)
    private Exemplaire exemplaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nom_emprunteur", nullable = false)
    private Users emprunteur;

    @Column(name = "date_debut_emprunt", nullable = false)
    private LocalDateTime dateDebutEmprunt;

    @Column(name = "date_fin_emprunt", nullable = false)
    private LocalDateTime dateFinEmprunt;

    @Column(name = "date_fin_proposee")
    private LocalDateTime dateFinProposee;

    @Column(name = "type_de_lecture")
    private String typeDeLecture;

    @Column(name = "is_prolongement", nullable = false)
    private boolean isProlongement = false;

    @Column(name = "nombre_prolongement", nullable = false)
    private int nombreProlongement = 0;

    @Column(name = "prolongement_demande", nullable = false)
    private boolean prolongementDemande = false;

    @Column(name = "prolongement_valide")
    private Boolean prolongementValide;

    @Column(name = "motif_refus_prolongement")
    private String motifRefusProlongement;



    public Emprunt() {}

    public Emprunt(Users emprunteur, Exemplaire exemplaire, LocalDateTime dateDebutEmprunt, LocalDateTime dateFinEmprunt, String typeDeLecture) {
        this.emprunteur = emprunteur;
        this.exemplaire = exemplaire;
        this.dateDebutEmprunt = dateDebutEmprunt;
        this.dateFinEmprunt = dateFinEmprunt;
        this.typeDeLecture = typeDeLecture;
        this.isProlongement = false;
        this.nombreProlongement = 0;
    }

    // Getters et setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exemplaire getExemplaires() { return exemplaire; }
    public void setExemplaires(Exemplaire exemplaire) { this.exemplaire = exemplaire; }

    public Users getEmprunteur() {
        return emprunteur;
    }
    public void setEmprunteur(Users emprunteur) {
        this.emprunteur = emprunteur;
    }

    public LocalDateTime getDateDebutEmprunt() { return dateDebutEmprunt; }
    public void setDateDebutEmprunt(LocalDateTime dateDebutEmprunt) { this.dateDebutEmprunt = dateDebutEmprunt; }

    public LocalDateTime getDateFinEmprunt() { return dateFinEmprunt; }
    public void setDateFinEmprunt(LocalDateTime dateFinEmprunt) { this.dateFinEmprunt = dateFinEmprunt; }
    

    public String getTypeDeLecture() {
        return typeDeLecture;
    }
    
    public void setTypeDeLecture(String typeDeLecture) {
        this.typeDeLecture = typeDeLecture;
    }

    public boolean isProlongement() { return isProlongement; }
    public void setProlongement(boolean isProlongement) { this.isProlongement = isProlongement; }

    public int getNombreProlongement() { return nombreProlongement; }
    public void setNombreProlongement(int nombreProlongement) { this.nombreProlongement = nombreProlongement; }
}