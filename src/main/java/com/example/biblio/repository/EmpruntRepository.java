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
    List<Emprunt> findByTypeDeLectureAndDateFinEmpruntIsNull(String typeDeLecture);
    List<Emprunt> findByDateFinEmpruntIsNotNullAndDateFinEmpruntAfter(LocalDateTime date);

    @Query("SELECT e FROM Emprunt e WHERE e.dateFinEmprunt IS NOT NULL AND e.dateFinEmprunt < :maintenant")
    List<Emprunt> findEmpruntsEnRetard(@Param("maintenant") LocalDateTime maintenant);

    // Nouvelle méthode pour trouver les emprunts par exemplaire
    List<Emprunt> findByExemplaire(Exemplaire exemplaire);
}