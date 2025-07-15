package com.example.biblio.repository;

import com.example.biblio.model.ParametresPenalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametresPenaliteRepository extends JpaRepository<ParametresPenalite, Long> {
    
    Optional<ParametresPenalite> findByNomParametre(String nomParametre);
    
    boolean existsByNomParametre(String nomParametre);
}
