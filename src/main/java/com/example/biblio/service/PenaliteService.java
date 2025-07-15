package com.example.biblio.service;

import com.example.biblio.config.PenaliteConfig;
import com.example.biblio.model.Emprunt;
import com.example.biblio.model.Penalite;
import com.example.biblio.model.ParametresPenalite;
import com.example.biblio.model.Users;
import com.example.biblio.repository.EmpruntRepository;
import com.example.biblio.repository.PenaliteRepository;
import com.example.biblio.repository.ParametresPenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class PenaliteService {

    @Autowired
    private PenaliteRepository penaliteRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private PenaliteConfig penaliteConfig;

    @Autowired
    private ParametresPenaliteRepository parametresPenaliteRepository;

    private static final String PARAM_JOURS_GRACE = "JOURS_GRACE";
    private static final String PARAM_PENALITE_BASE = "PENALITE_BASE_JOURS";
    private static final String PARAM_PENALITE_PAR_JOUR = "PENALITE_PAR_JOUR_RETARD";

    public void verifierEtAppliquerPenalites() {
        LocalDateTime maintenant = LocalDateTime.now();
        
        List<Emprunt> empruntsEnRetard = empruntRepository.findEmpruntsEnRetard(maintenant);
        
        for (Emprunt emprunt : empruntsEnRetard) {
            if (!aPenalitePourEmprunt(emprunt)) {
                creerPenalitePourEmprunt(emprunt, maintenant);
            }
        }
        
        mettreAJourPenalitesExpirees();
    }

   
    public boolean utilisateurAPenaliteActive(Users utilisateur) {
        return penaliteRepository.hasActivePenalty(utilisateur, LocalDateTime.now());
    }

  
    public List<Penalite> getPenalitesActives(Users utilisateur) {
        return penaliteRepository.findPenalitesActivesForUser(utilisateur, LocalDateTime.now());
    }

    
    private void creerPenalitePourEmprunt(Emprunt emprunt, LocalDateTime maintenant) {
        if (emprunt.getDateFinEmprunt() == null) {
            return; // Pas de pénalité pour les emprunts sur place
        }

        long joursRetard = ChronoUnit.DAYS.between(emprunt.getDateFinEmprunt(), maintenant);
        
        int joursGrace = getParametreValue(PARAM_JOURS_GRACE, penaliteConfig.getJoursGrace());
        if (joursRetard > joursGrace) {
            int joursRetardEffectif = (int) (joursRetard - joursGrace);
            int penaliteBase = getParametreValue(PARAM_PENALITE_BASE, penaliteConfig.getPenaliteBaseJours());
            int penaliteParJour = getParametreValue(PARAM_PENALITE_PAR_JOUR, penaliteConfig.getPenaliteParJourRetard());
            int dureePenalite = penaliteBase + (joursRetardEffectif * penaliteParJour);
            
            String motif = String.format("Exemplaire '%s' du livre '%s' rendu avec %d jours de retard", 
                emprunt.getExemplaire().getNumExemplaire(), 
                emprunt.getExemplaire().getLivre().getTitre(), 
                joursRetardEffectif);

            Penalite penalite = new Penalite(
                emprunt.getEmprunteur(), 
                emprunt, 
                joursRetardEffectif, 
                dureePenalite, 
                motif
            );

            penaliteRepository.save(penalite);
        }
    }

  
    private boolean aPenalitePourEmprunt(Emprunt emprunt) {
        List<Penalite> penalites = penaliteRepository.findByUtilisateur(emprunt.getEmprunteur());
        return penalites.stream().anyMatch(p -> p.getEmprunt().getId().equals(emprunt.getId()));
    }

   
    private void mettreAJourPenalitesExpirees() {
        List<Penalite> penalitesExpirees = penaliteRepository.findPenalitesExpired(LocalDateTime.now());
        
        for (Penalite penalite : penalitesExpirees) {
            penalite.setStatut("TERMINEE");
            penaliteRepository.save(penalite);
        }
    }

    
    public List<Penalite> getPenalitesUtilisateur(Users utilisateur) {
        return penaliteRepository.findByUtilisateur(utilisateur);
    }

 
    public String getParametresPenalites() {
        initialiserParametresParDefaut();
        List<ParametresPenalite> parametres = getTousLesParametres();
        
        int joursGrace = parametres.stream()
                .filter(p -> PARAM_JOURS_GRACE.equals(p.getNomParametre()))
                .findFirst()
                .map(ParametresPenalite::getValeur)
                .orElse(penaliteConfig.getJoursGrace());
                
        int penaliteBase = parametres.stream()
                .filter(p -> PARAM_PENALITE_BASE.equals(p.getNomParametre()))
                .findFirst()
                .map(ParametresPenalite::getValeur)
                .orElse(penaliteConfig.getPenaliteBaseJours());
                
        int penaliteParJour = parametres.stream()
                .filter(p -> PARAM_PENALITE_PAR_JOUR.equals(p.getNomParametre()))
                .findFirst()
                .map(ParametresPenalite::getValeur)
                .orElse(penaliteConfig.getPenaliteParJourRetard());
        
        return String.format("Période de grâce: %d jours, Pénalité de base: %d jours, Pénalité par jour de retard: %d jours",
            joursGrace, penaliteBase, penaliteParJour);
    }

    /**
     * Initialise les paramètres par défaut s'ils n'existent pas
     */
    public void initialiserParametresParDefaut() {
        if (!parametresPenaliteRepository.existsByNomParametre(PARAM_JOURS_GRACE)) {
            parametresPenaliteRepository.save(new ParametresPenalite(PARAM_JOURS_GRACE, 3, "Nombre de jours de grâce avant application d'une pénalité"));
        }
        if (!parametresPenaliteRepository.existsByNomParametre(PARAM_PENALITE_BASE)) {
            parametresPenaliteRepository.save(new ParametresPenalite(PARAM_PENALITE_BASE, 7, "Durée de base de la pénalité (en jours)"));
        }
        if (!parametresPenaliteRepository.existsByNomParametre(PARAM_PENALITE_PAR_JOUR)) {
            parametresPenaliteRepository.save(new ParametresPenalite(PARAM_PENALITE_PAR_JOUR, 2, "Jours supplémentaires par jour de retard"));
        }
    }

  
    private int getParametreValue(String nomParametre, int defaultValue) {
        return parametresPenaliteRepository.findByNomParametre(nomParametre)
                .map(ParametresPenalite::getValeur)
                .orElse(defaultValue);
    }

    public List<ParametresPenalite> getTousLesParametres() {
        initialiserParametresParDefaut();
        return parametresPenaliteRepository.findAll();
    }

    public void mettreAJourParametre(String nomParametre, Integer nouvelleValeur) {
        ParametresPenalite parametre = parametresPenaliteRepository.findByNomParametre(nomParametre)
                .orElseThrow(() -> new RuntimeException("Paramètre non trouvé: " + nomParametre));
        parametre.setValeur(nouvelleValeur);
        parametresPenaliteRepository.save(parametre);
    }
}
