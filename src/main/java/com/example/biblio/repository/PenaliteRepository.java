package com.example.biblio.repository;

import com.example.biblio.model.Penalite;
import com.example.biblio.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    List<Penalite> findByUtilisateur(Users utilisateur);

    @Query("SELECT p FROM Penalite p WHERE p.utilisateur = :utilisateur AND p.statut = 'ACTIVE' AND p.dateFinPenalite > :now")
    List<Penalite> findPenalitesActivesForUser(@Param("utilisateur") Users utilisateur, @Param("now") LocalDateTime now);

    @Query("SELECT p FROM Penalite p WHERE p.statut = 'ACTIVE' AND p.dateFinPenalite <= :now")
    List<Penalite> findPenalitesExpired(@Param("now") LocalDateTime now);

    List<Penalite> findByStatut(String statut);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Penalite p WHERE p.utilisateur = :utilisateur AND p.statut = 'ACTIVE' AND p.dateFinPenalite > :now")
    boolean hasActivePenalty(@Param("utilisateur") Users utilisateur, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) > 0 FROM Penalite p WHERE p.utilisateur.id = :userId AND p.dateFinPenalite > :date")
    boolean existsByUserIdAndDateFinPenaliteAfter(@Param("userId") Long userId, @Param("date") LocalDateTime date);

}