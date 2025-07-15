package com.example.biblio.service;

import com.example.biblio.model.*;
import com.example.biblio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmpruntService {

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private PenaliteService penaliteService;

    public Emprunt creerEmprunt(Long idLivre, Long idEmprunteur, String typeEmprunt, 
                               LocalDateTime dateDebutEmprunt, LocalDateTime dateFinProposee) {
        try {
            System.out.println("Début création emprunt - Livre: " + idLivre + ", User: " + idEmprunteur);
            
            Livre livre = livreRepository.findById(idLivre)
                    .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
            System.out.println("Livre trouvé: " + livre.getTitre());
            
            Users emprunteur = usersRepository.findById(idEmprunteur)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            System.out.println("Utilisateur trouvé: " + emprunteur.getUserName());

            if (penaliteService.utilisateurAPenaliteActive(emprunteur)) {
                throw new RuntimeException("penalite_active");
            }

            List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreAndDisponibleTrue(livre);
            if (exemplairesDisponibles.isEmpty()) {
                throw new RuntimeException("no_exemplaire");
            }

            Exemplaire exemplaire = exemplairesDisponibles.get(0);
            exemplaire.setDisponible(false);
            exemplaireRepository.save(exemplaire);

            Emprunt emprunt = new Emprunt();
            emprunt.setExemplaire(exemplaire);
            emprunt.setEmprunteur(emprunteur);
            emprunt.setDateDebutEmprunt(dateDebutEmprunt);
            emprunt.setDateFinEmprunt(typeEmprunt.equals("SUR_PLACE") ? LocalDateTime.now().plusDays(1) : dateFinProposee);
            emprunt.setDateFinProposee(typeEmprunt.equals("SUR_PLACE") ? null : dateFinProposee);
            emprunt.setTypeDeLecture(typeEmprunt);
            emprunt.setProlongement(false);
            emprunt.setNombreProlongement(0);
            emprunt.setProlongementDemande(false);

            return empruntRepository.save(emprunt);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création d'emprunt: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void retournerLivre(Long empruntId, LocalDateTime dateRetour) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
    
        if (dateRetour.isBefore(emprunt.getDateDebutEmprunt())) {
            throw new RuntimeException("La date de retour ne peut pas être antérieure à la date de début de l'emprunt.");
        }
    
        emprunt.setDateRetourEffective(dateRetour);
    
        emprunt.setDateFinEmprunt(dateRetour);
    
        Exemplaire exemplaire = emprunt.getExemplaire();
        exemplaire.setDisponible(true);
        exemplaireRepository.save(exemplaire);
    
        empruntRepository.save(emprunt);
    }
    
    
}