package com.example.biblio.controller;

import com.example.biblio.model.Penalite;
import com.example.biblio.model.Users;
import com.example.biblio.repository.UsersRepository;
import com.example.biblio.service.PenaliteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/penalite")
public class PenaliteController {

    @Autowired
    private PenaliteService penaliteService;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/gestion")
    public String afficherGestionPenalites(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Users admin = usersRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }

        // Vérifier et appliquer les pénalités en cours
        penaliteService.verifierEtAppliquerPenalites();

        // Récupérer toutes les pénalités actives
        List<Users> tousLesUtilisateurs = usersRepository.findAll();
        model.addAttribute("utilisateurs", tousLesUtilisateurs);
        model.addAttribute("parametres", penaliteService.getParametresPenalites());
        
        boolean isAdmin = "admin".equalsIgnoreCase(admin.getProfilFormule().getProfil());
        model.addAttribute("isAdmin", isAdmin);

        return "gestion-penalites";
    }

    @GetMapping("/mes-penalites")
    public String afficherMesPenalites(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Penalite> penalites = penaliteService.getPenalitesUtilisateur(user);
        List<Penalite> penalitesActives = penaliteService.getPenalitesActives(user);

        model.addAttribute("penalites", penalites);
        model.addAttribute("penalitesActives", penalitesActives);
        model.addAttribute("user", user);
        
        boolean isAdmin = "admin".equalsIgnoreCase(user.getProfilFormule().getProfil());
        model.addAttribute("isAdmin", isAdmin);

        return "mes-penalites";
    }

    @PostMapping("/verifier")
    public String verifierPenalites(RedirectAttributes redirectAttributes, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Users admin = usersRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }

        penaliteService.verifierEtAppliquerPenalites();
        redirectAttributes.addFlashAttribute("success", "Vérification des pénalités effectuée avec succès !");

        return "redirect:/penalite/gestion";
    }

    @GetMapping("/parametres")
    public String afficherParametres(Model model, HttpSession session) {
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
        model.addAttribute("config", penaliteService.getParametresPenalites());
        model.addAttribute("parametres", penaliteService.getTousLesParametres());

        return "parametres-penalites";
    }

    @PostMapping("/parametres/modifier")
    public String modifierParametres(
            @RequestParam String parametre,
            @RequestParam Integer valeur,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Users admin = usersRepository.findById(userId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getProfilFormule().getProfil())) {
            return "redirect:/livres?error=notadmin";
        }

        try {
            penaliteService.mettreAJourParametre(parametre, valeur);
            redirectAttributes.addFlashAttribute("success", 
                String.format("Paramètre '%s' mis à jour avec la valeur %d", parametre, valeur));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la mise à jour: " + e.getMessage());
        }

        return "redirect:/penalite/parametres";
    }
}
