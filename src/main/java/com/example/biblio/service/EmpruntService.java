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

    public Emprunt creerEmprunt(Long idLivre, Long idEmprunteur, String typeEmprunt) {
        // Récupérer le livre et l'utilisateur
        Livre livre = livreRepository.findById(idLivre)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        Users emprunteur = usersRepository.findById(idEmprunteur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur a une pénalité active
        if (penaliteService.utilisateurAPenaliteActive(emprunteur)) {
            throw new RuntimeException("L'utilisateur a une pénalité active");
        }

        // Vérifier la disponibilité d'un exemplaire
        List<Exemplaire> exemplairesDisponibles = exemplaireRepository.findByLivreAndDisponibleTrue(livre);
        if (exemplairesDisponibles.isEmpty()) {
            throw new RuntimeException("Aucun exemplaire disponible pour ce livre");
        }

        // Prendre le premier exemplaire disponible
        Exemplaire exemplaire = exemplairesDisponibles.get(0);
        exemplaire.setDisponible(false); // Marquer comme non disponible
        exemplaireRepository.save(exemplaire);

        // Obtenir le profil de l'utilisateur
        ProfilFormule profil = emprunteur.getProfilFormule();
        if (profil == null) {
            throw new RuntimeException("L'utilisateur n'a pas de profil associé");
        }

        // Créer l'emprunt
        LocalDateTime dateDebut = LocalDateTime.now();
        LocalDateTime dateFin = typeEmprunt.equals("A_EMPORTER") ? dateDebut.plusMonths(profil.getNombreDeMois()) : null;

        Emprunt emprunt = new Emprunt();
        emprunt.setExemplaires(exemplaire);
        emprunt.setEmprunteur(emprunteur);
        emprunt.setDateDebutEmprunt(dateDebut);
        emprunt.setDateFinEmprunt(dateFin);
        emprunt.setTypeDeLecture(typeEmprunt);
        emprunt.setProlongement(false);
        emprunt.setNombreProlongement(0);

        return empruntRepository.save(emprunt);
    }

    public void retournerLivre(Long empruntId) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));

        if (emprunt.getDateFinEmprunt() != null && emprunt.getDateFinEmprunt().isBefore(LocalDateTime.now())) {
            penaliteService.verifierEtAppliquerPenalites(); // Vérifier les pénalités en cas de retard
        }

        // Mettre à jour la date de fin pour les emprunts sur place
        if ("SUR_PLACE".equals(emprunt.getTypeDeLecture())) {
            emprunt.setDateFinEmprunt(LocalDateTime.now());
        }

        // Marquer l'exemplaire comme disponible
        Exemplaire exemplaire = emprunt.getExemplaires();
        exemplaire.setDisponible(true);
        exemplaireRepository.save(exemplaire);

        empruntRepository.save(emprunt);
    }
}