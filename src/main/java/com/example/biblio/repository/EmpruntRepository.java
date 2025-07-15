package com.example.biblio.repository;

import com.example.biblio.model.Emprunt;
import com.example.biblio.model.Exemplaire;
import com.example.biblio.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByEmprunteurAndDateFinEmpruntIsNull(Users emprunteur);
    long countByEmprunteurAndDateFinEmpruntIsNull(Users emprunteur);

    @Query("SELECT e FROM Emprunt e WHERE e.typeDeLecture = :type AND e.dateFinEmprunt > :now")
    List<Emprunt> findActiveByTypeDeLecture(@Param("type") String typeDeLecture, @Param("now") LocalDateTime now);

    List<Emprunt> findByDateFinEmpruntIsNotNullAndDateFinEmpruntAfter(LocalDateTime date);

    @Query("SELECT e FROM Emprunt e WHERE e.dateFinEmprunt IS NOT NULL AND e.dateFinEmprunt < :maintenant")
    List<Emprunt> findEmpruntsEnRetard(@Param("maintenant") LocalDateTime maintenant);

    List<Emprunt> findByExemplaire(Exemplaire exemplaire);

    @Query("SELECT e FROM Emprunt e JOIN FETCH e.exemplaire ex JOIN FETCH ex.livre JOIN FETCH e.emprunteur")
    List<Emprunt> findAllWithDetails();

    @Query("SELECT e FROM Emprunt e JOIN FETCH e.exemplaire ex JOIN FETCH ex.livre JOIN FETCH e.emprunteur WHERE e.emprunteur.id = :userId")
    List<Emprunt> findByEmprunteurIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT e FROM Emprunt e WHERE e.dateRetourEffective IS NULL")
    List<Emprunt> findAllNonRetournés();

    @Query("SELECT e FROM Emprunt e JOIN FETCH e.exemplaire ex JOIN FETCH ex.livre WHERE e.dateRetourEffective IS NULL")
    List<Emprunt> findAllWithDetailsNotReturned();

    List<Emprunt> findByEmprunteurIdAndDateRetourEffectiveIsNull(Long emprunteurId);

    int countByEmprunteurIdAndDateRetourEffectiveIsNull(Long emprunteurId);
}