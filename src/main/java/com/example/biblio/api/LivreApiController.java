package com.example.biblio.api;

import com.example.biblio.model.Exemplaire;
import com.example.biblio.model.Livre;
import com.example.biblio.repository.ExemplaireRepository;
import com.example.biblio.repository.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livres")
public class LivreApiController {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/{id}")
    public Map<String, Object> getLivreAvecExemplaires(@PathVariable Long id) {
        Livre livre = livreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));

        List<Exemplaire> exemplaires = exemplaireRepository.findByLivreId(id);

        List<Map<String, Object>> exemplaireInfos = exemplaires.stream()
                .map(ex -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", ex.getId());
                    info.put("numExemplaire", ex.getNumExemplaire());
                    info.put("disponible", ex.isDisponible());
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("id", livre.getId());
        response.put("titre", livre.getTitre());
        response.put("auteur", livre.getAuteur());
        response.put("exemplaires", exemplaireInfos);

        return response;
    }

    @GetMapping("/{id}/exemplaires")
    public List<Map<String, Object>> getExemplairesByLivreId(@PathVariable Long id) {
        livreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));

        List<Exemplaire> exemplaires = exemplaireRepository.findByLivreId(id);

        return exemplaires.stream()
                .map(ex -> {
                    Map<String, Object> exemplaireMap = new HashMap<>();
                    exemplaireMap.put("id", ex.getId());
                    exemplaireMap.put("numExemplaire", ex.getNumExemplaire());
                    exemplaireMap.put("disponible", ex.isDisponible());
                    return exemplaireMap;
                })
                .collect(Collectors.toList());
    }
}