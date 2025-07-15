package com.example.biblio.api;

import com.example.biblio.model.Users;
import com.example.biblio.model.ProfilFormule;
import com.example.biblio.repository.UsersRepository;
import com.example.biblio.repository.EmpruntRepository;
import com.example.biblio.repository.ReservationRepository;
import com.example.biblio.repository.PenaliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/adherents")
public class AdherentApiController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    @GetMapping("/{idUser}/statut")
    public Map<String, Object> getAdherentStatut(@PathVariable Long idUser) {
        Users user = usersRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Map<String, Object> response = new HashMap<>();

        ProfilFormule profil = user.getProfilFormule();
        boolean isAbonne = profil != null && profil.getNombreDeMois() > 0;
        response.put("estAbonne", isAbonne);

        int pretCount = empruntRepository.countByEmprunteurIdAndDateRetourEffectiveIsNull(idUser);
        int reservationCount = reservationRepository.countByUtilisateurIdAndStatutIn(idUser, List.of("EN_ATTENTE", "VALIDE"));
        int quotaMax = profil != null ? profil.getNblivrePort() : 0;
        response.put("quotaPretDisponible", quotaMax - pretCount);
        response.put("quotaReservationDisponible", quotaMax - reservationCount);

        boolean isSanctionne = penaliteRepository.existsByUserIdAndDateFinPenaliteAfter(idUser, LocalDateTime.now());
        response.put("estSanctionne", isSanctionne);

        response.put("idUser", user.getId());
        response.put("userName", user.getUserName());
        response.put("profil", profil != null ? profil.getProfil() : null);

        return response;
    }

    @GetMapping
    public List<Users> getAllAdherents() {
        return usersRepository.findAll();
    }
}