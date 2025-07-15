package com.example.biblio.repository;

import com.example.biblio.model.Exemplaire;
import com.example.biblio.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    List<Exemplaire> findByLivreAndDisponibleTrue(Livre livre);

    Optional<Exemplaire> findByNumExemplaire(String numExemplaire);

    @Query("SELECT COUNT(e) FROM Exemplaire e WHERE e.livre.id = :livreId AND e.disponible = true")
    Integer countAvailableExemplaires(@Param("livreId") Long livreId);

    @Query("SELECT e FROM Exemplaire e WHERE e.livre.id = :livreId AND e.disponible = true")
    List<Exemplaire> findByLivreIdAndDisponibleTrue(@Param("livreId") Long livreId);

    List<Exemplaire> findByLivreId(Long livreId);


}