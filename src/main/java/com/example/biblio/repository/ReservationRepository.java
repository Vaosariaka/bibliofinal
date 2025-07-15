package com.example.biblio.repository;

import com.example.biblio.model.Reservation;
import com.example.biblio.model.Users;
import com.example.biblio.model.Livre;
import com.example.biblio.model.Exemplaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateur(Users utilisateur);
    List<Reservation> findByLivre(Livre livre);
    Optional<Reservation> findByUtilisateurAndLivreAndStatut(Users utilisateur, Livre livre, String statut);
    List<Reservation> findByStatut(String statut);
    Optional<Reservation> findByLivreAndStatut(Livre livre, String statut);
    List<Reservation> findByExemplaireAttribue(Exemplaire exemplaire);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.utilisateur.id = :userId AND r.statut IN :statuts")
    int countByUserIdAndStatutIn(@Param("userId") Long userId, @Param("statuts") List<String> statuts);

    // Méthode alternative sans @Query
    int countByUtilisateurIdAndStatutIn(Long utilisateurId, List<String> statuts);
}