package com.example.biblio.controller;

import com.example.biblio.model.*;
import com.example.biblio.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @PostMapping("/creer")
    public String creerReservation(@RequestParam Long livreId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour réserver un livre");
            return "redirect:/login";
        }
        try {
            Optional<Users> userOpt = usersRepository.findById(userId);
            Optional<Livre> livreOpt = livreRepository.findById(livreId);
            if (userOpt.isEmpty() || livreOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur ou livre non trouvé");
                return "redirect:/livres";
            }
            Users user = userOpt.get();
            Livre livre = livreOpt.get();
            Optional<Reservation> reservationExistante = reservationRepository.findByLivreAndStatut(livre, "ACTIVE");
            if (reservationExistante.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ce livre est déjà réservé");
                return "redirect:/livres";
            }
            Optional<Reservation> reservationUser = reservationRepository.findByUtilisateurAndLivreAndStatut(user, livre, "ACTIVE");
            if (reservationUser.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Vous avez déjà réservé ce livre");
                return "redirect:/livres";
            }
            Reservation reservation = new Reservation(user, livre);
            reservationRepository.save(reservation);
            redirectAttributes.addFlashAttribute("success", "Livre réservé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réservation: " + e.getMessage());
        }
        return "redirect:/livres";
    }

    @PostMapping("/annuler/{id}")
    public String annulerReservation(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            Optional<Reservation> reservationOpt = reservationRepository.findById(id);
            if (reservationOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Réservation non trouvée");
                return "redirect:/livres";
            }
            Reservation reservation = reservationOpt.get();
            if (!reservation.getUtilisateur().getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Vous ne pouvez annuler que vos propres réservations");
                return "redirect:/livres";
            }
            reservation.setStatut("ANNULEE");
            if (reservation.getExemplaireAttribue() != null) {
                Exemplaire exemplaire = reservation.getExemplaireAttribue();
                exemplaire.setDisponible(true);
                exemplaireRepository.save(exemplaire);
            }
            reservationRepository.save(reservation);
            redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation: " + e.getMessage());
        }
        return "redirect:/livres";
    }

    @GetMapping("/mes-reservations")
    public String mesReservations(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<Users> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        Users user = userOpt.get();
        List<Reservation> reservations = reservationRepository.findByUtilisateur(user);
        boolean isAdmin = "admin".equalsIgnoreCase(user.getProfilFormule().getProfil());
        model.addAttribute("reservations", reservations);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        return "mes-reservations";
    }
}