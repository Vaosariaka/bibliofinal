package com.example.biblio.controller;

import com.example.biblio.model.*;
import com.example.biblio.repository.*;
import com.example.biblio.service.EmpruntService;
import com.example.biblio.service.PenaliteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EmpruntViewController {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private PenaliteRepository penaliteRepository;

    @Autowired
    private EmpruntService empruntService;

    @Autowired
    private PenaliteService penaliteService;

    @GetMapping("/emprunt/nouveau")
    public String afficherFormulaireEmprunt(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        Users admin = usersRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }
        // Charger les livres ayant des exemplaires disponibles
        List<Livre> livres = livreRepository.findAll();
        model.addAttribute("livresDisponibles", livres);
        model.addAttribute("utilisateurs", usersRepository.findAll());
        return "emprunt-form";
    }

    @PostMapping("/emprunt/creer")
    public String creerEmprunt(
            @RequestParam Long idLivre,
            @RequestParam String typeEmprunt,
            @RequestParam Long userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long adminId = (Long) session.getAttribute("userId");
        if (adminId == null) {
            return "redirect:/login";
        }
        Users admin = usersRepository.findById(adminId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }
        try {
            empruntService.creerEmprunt(idLivre, userId, typeEmprunt);
            return "redirect:/livres?success=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/emprunt/nouveau";
        }
    }

    @PostMapping("/emprunt/{id}/retour")
    public String retournerLivre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empruntService.retournerLivre(id);
            redirectAttributes.addFlashAttribute("success", "Livre retourné avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
        }
        return "redirect:/livres";
    }

    @GetMapping("/emprunt/retour-sur-place")
    public String afficherRetourSurPlace(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        boolean isAdmin = "admin".equalsIgnoreCase(user.getProfilFormule().getProfil());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("empruntsSurPlace", empruntRepository.findByTypeDeLectureAndDateFinEmpruntIsNull("SUR_PLACE"));
        return "retour-sur-place";
    }

    @PostMapping("/emprunt/retour-sur-place")
    public String validerRetourSurPlace(@RequestParam Long empruntId, RedirectAttributes redirectAttributes) {
        try {
            empruntService.retournerLivre(empruntId);
            redirectAttributes.addFlashAttribute("success", "Retour validé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
        }
        return "redirect:/emprunt/retour-sur-place";
    }

    @GetMapping("/emprunt/prolongement")
    public String afficherFormulaireProlongement(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        Users admin = usersRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }
        boolean isAdmin = "admin".equalsIgnoreCase(admin.getProfilFormule().getProfil());
        model.addAttribute("isAdmin", isAdmin);
        List<Emprunt> empruntsActifs = empruntRepository.findByDateFinEmpruntIsNotNullAndDateFinEmpruntAfter(LocalDateTime.now());
        model.addAttribute("empruntsActifs", empruntsActifs);
        return "prolongement-form";
    }

    @PostMapping("/emprunt/prolonger")
    public String prolongerEmprunt(
            @RequestParam Long empruntId,
            @RequestParam int moisSupplementaires,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long adminId = (Long) session.getAttribute("userId");
        if (adminId == null) {
            return "redirect:/login";
        }
        Users admin = usersRepository.findById(adminId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }
        try {
            Emprunt emprunt = empruntRepository.findById(empruntId)
                    .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
            if (emprunt.getDateFinEmprunt() == null || emprunt.getDateFinEmprunt().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Cet emprunt n'est plus actif");
                return "redirect:/emprunt/prolongement";
            }
            LocalDateTime nouvelleDateFin = emprunt.getDateFinEmprunt().plusMonths(moisSupplementaires);
            emprunt.setDateFinEmprunt(nouvelleDateFin);
            emprunt.setProlongement(true);
            emprunt.setNombreProlongement(emprunt.getNombreProlongement() + 1);
            empruntRepository.save(emprunt);
            redirectAttributes.addFlashAttribute("success",
                    String.format("Emprunt prolongé de %d mois. Nouvelle date de fin: %s",
                            moisSupplementaires, nouvelleDateFin.toLocalDate()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la prolongation: " + e.getMessage());
        }
        return "redirect:/emprunt/prolongement";
    }
}