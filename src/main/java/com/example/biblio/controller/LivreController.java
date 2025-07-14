package com.example.biblio.controller;

import com.example.biblio.model.*;
import com.example.biblio.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class LivreController {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping("/livres")
    public String afficherLivres(Model model, HttpSession session) {
        List<Livre> livres = livreRepository.findAll();
        Map<Long, Integer> exemplairesDisponibles = new HashMap<>();
        for (Livre livre : livres) {
            int count = exemplaireRepository.findByLivreAndDisponibleTrue(livre).size();
            exemplairesDisponibles.put(livre.getId(), count);
        }
        model.addAttribute("livres", livres);
        model.addAttribute("exemplairesDisponibles", exemplairesDisponibles);

        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            Optional<Users> userOpt = usersRepository.findById(userId);
            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                model.addAttribute("currentUser", user);
                boolean isAdmin = "admin".equalsIgnoreCase(user.getProfilFormule().getProfil());
                model.addAttribute("isAdmin", isAdmin);

                Map<Long, Reservation> reservationsParLivre = new HashMap<>();
                for (Livre livre : livres) {
                    Optional<Reservation> reservation = reservationRepository.findByLivreAndStatut(livre, "ACTIVE");
                    if (reservation.isPresent()) {
                        reservationsParLivre.put(livre.getId(), reservation.get());
                    }
                }
                model.addAttribute("reservationsParLivre", reservationsParLivre);
            }
        }
        return "livres";
    }
}