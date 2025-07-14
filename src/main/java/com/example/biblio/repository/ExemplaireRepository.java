package com.example.biblio.repository;

import com.example.biblio.model.Exemplaire;
import com.example.biblio.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    List<Exemplaire> findByLivreAndDisponibleTrue(Livre livre);
    Optional<Exemplaire> findByNumExemplaire(String numExemplaire);
}