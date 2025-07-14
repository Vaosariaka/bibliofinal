package com.example.biblio.controller;

import com.example.biblio.model.Users;
import com.example.biblio.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsersControlleur {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProfilFormuleRepository profilFormuleRepository;

    @GetMapping("/")
    public String redirigerVersLogin() {
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String afficherFormulaireLogin(Model model,
        @RequestParam(required = false) String error,
        @RequestParam(required = false) String success) {
        if (error != null) {
            model.addAttribute("errorMessage", "Identifiants incorrects");
        }
        if (success != null) {
            model.addAttribute("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String traiterFormulaireLogin(@RequestParam String userName,
                                       @RequestParam String motDePasse,
                                       HttpSession session) {
        Users login = usersRepository.findByUserNameAndMotDePasse(userName, motDePasse);
        if (login != null) {
            // Stocker l'ID utilisateur en session comme Long
            session.setAttribute("userId", login.getId().longValue());
            return "redirect:/livres";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/abonnement-expire")
    public String abonnementExpire() {
        return "abonnement-expire";
    }

    // @PostMapping("/login")
    // public String traiterFormulaireLogin(@RequestParam String userName,
    //                                     @RequestParam String motDePasse,
    //                                     Model model,
    //                                     HttpSession session) {
    //     Sign_in login = usersRepository.findByUserNameAndMotDePasse(userName, motDePasse);
    //     if (login != null) {
    //         // Stocke le profil dans la session
    //         session.setAttribute("id", login.getId());
    //         session.setAttribute("profil", login.getProfil());
    //         if ("admin".equalsIgnoreCase(login.getProfil())) {
    //             return "redirect:/livres";
    //         }
    //         Abonnement abn = abonnementRepository.findByNomPersonne(userName);
    //         if (abn == null || abn.getFinAbonnement() == null || abn.getFinAbonnement().isBefore(LocalDate.now())) {
    //             // Abonnement expiré ou inexistant
    //             return "redirect:/abonnement-expire";
    //         }
    //         // Abonnement valide
    //         return "redirect:/livres";
    //     } else {
    //         return "redirect:/login?error=true";
    //     }
    // }

    @GetMapping("/inscription")
    public String afficherFormulaireInscription(Model model) {
        model.addAttribute("users", new Users());
        model.addAttribute("profilFormules", profilFormuleRepository.findAll());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String traiterFormulaireInscription(@ModelAttribute Users users, Model model) {
        try {
            usersRepository.save(users);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'inscription. Veuillez réessayer.");
            model.addAttribute("users", users);
            model.addAttribute("profilFormules", profilFormuleRepository.findAll());
            return "inscription";
        }
    }

    @GetMapping("/logout")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String deconnexionPost(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
