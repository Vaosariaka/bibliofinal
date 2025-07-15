package com.example.biblio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prolongement")
public class Prolongement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emprunt_id", nullable = false)
    private Emprunt emprunt;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_fin_originale", nullable = false)
    private LocalDateTime dateFinOriginale;

    @Column(name = "date_fin_proposee", nullable = false)
    private LocalDateTime dateFinProposee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "valide_par_admin")
    private Users valideParAdmin;

    @Column(name = "statut", nullable = false)
    private String statut = "EN_ATTENTE"; // EN_ATTENTE, VALIDE, REFUSE

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "motif_refus")
    private String motifRefus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emprunt getEmprunt() {
        return emprunt;
    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDateTime getDateFinOriginale() {
        return dateFinOriginale;
    }

    public void setDateFinOriginale(LocalDateTime dateFinOriginale) {
        this.dateFinOriginale = dateFinOriginale;
    }

    public LocalDateTime getDateFinProposee() {
        return dateFinProposee;
    }

    public void setDateFinProposee(LocalDateTime dateFinProposee) {
        this.dateFinProposee = dateFinProposee;
    }

    public Users getValideParAdmin() {
        return valideParAdmin;
    }

    public void setValideParAdmin(Users valideParAdmin) {
        this.valideParAdmin = valideParAdmin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }

   
    // Constructeur par défaut (requis par JPA)
public Prolongement() {
    this.dateDemande = LocalDateTime.now();
    this.statut = "EN_ATTENTE";
}

// Constructeur pour une nouvelle demande de prolongement
public Prolongement(Emprunt emprunt, LocalDateTime dateFinProposee) {
    this();
    this.emprunt = emprunt;
    this.dateFinOriginale = emprunt.getDateFinEmprunt();
    this.dateFinProposee = dateFinProposee;
}

// Constructeur complet (pour initialiser toutes les propriétés)
public Prolongement(Long id, Emprunt emprunt, LocalDateTime dateDemande, 
                   LocalDateTime dateFinOriginale, LocalDateTime dateFinProposee,
                   Users valideParAdmin, String statut, 
                   LocalDateTime dateValidation, String motifRefus) {
    this.id = id;
    this.emprunt = emprunt;
    this.dateDemande = dateDemande;
    this.dateFinOriginale = dateFinOriginale;
    this.dateFinProposee = dateFinProposee;
    this.valideParAdmin = valideParAdmin;
    this.statut = statut;
    this.dateValidation = dateValidation;
    this.motifRefus = motifRefus;
}

// Méthode utilitaire pour valider un prolongement
public void valider(Users admin) {
    this.statut = "VALIDE";
    this.valideParAdmin = admin;
    this.dateValidation = LocalDateTime.now();
    this.motifRefus = null;
}

// Méthode utilitaire pour refuser un prolongement
public void refuser(Users admin, String motif) {
    this.statut = "REFUSE";
    this.valideParAdmin = admin;
    this.dateValidation = LocalDateTime.now();
    this.motifRefus = motif;
}

// Méthode pour vérifier si le prolongement est en attente
public boolean isEnAttente() {
    return "EN_ATTENTE".equals(this.statut);
}

// Méthode pour vérifier si le prolongement est validé
public boolean isValide() {
    return "VALIDE".equals(this.statut);
}

// Méthode pour vérifier si le prolongement est refusé
public boolean isRefuse() {
    return "REFUSE".equals(this.statut);
}
}