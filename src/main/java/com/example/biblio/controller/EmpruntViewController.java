package com.example.biblio.controller;

import com.example.biblio.model.*;
import com.example.biblio.repository.*;
import com.example.biblio.service.EmpruntService;
import com.example.biblio.service.PenaliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;


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
    private PenaliteService penaliteService;

    @Autowired
    private EmpruntService empruntService;

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

        List<Livre> livres = livreRepository.findAll();
        Map<Long, Integer> exemplairesDisponibles = new HashMap<>();
        for (Livre livre : livres) {
            Integer available = exemplaireRepository.countAvailableExemplaires(livre.getId());
            exemplairesDisponibles.put(livre.getId(), available != null ? available : 0);
        }

        model.addAttribute("livresDisponibles", livres);
        model.addAttribute("exemplairesDisponibles", exemplairesDisponibles);
        model.addAttribute("utilisateurs", usersRepository.findAll());
        return "emprunt-form";
    }

    @PostMapping("/emprunt/creer")
    public String creerEmprunt(
            @RequestParam Long idLivre,
            @RequestParam String typeEmprunt,
            @RequestParam Long userId,
            @RequestParam String dateDebutEmprunt,
            @RequestParam(required = false) String dateFinEmprunt,
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
            LocalDateTime debut = LocalDateTime.parse(dateDebutEmprunt);
            LocalDateTime finProposee = typeEmprunt.equals("SUR_PLACE") ? null : LocalDateTime.parse(dateFinEmprunt);
            empruntService.creerEmprunt(idLivre, userId, typeEmprunt, debut, finProposee);
            return "redirect:/livres?success=true";
        } catch (RuntimeException e) {
            if ("penalite_active".equals(e.getMessage())) {
                redirectAttributes.addFlashAttribute("error", "L'utilisateur a une pénalité active et ne peut pas emprunter");
            } else if ("no_exemplaire".equals(e.getMessage())) {
                redirectAttributes.addFlashAttribute("error", "Aucun exemplaire disponible pour ce livre");
            } else {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/emprunt/nouveau";
        }
    }

   @PostMapping("/emprunt/{id}/retour")
public String retournerLivre(
        @PathVariable Long id,
        @RequestParam("dateRetour") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRetour,
        RedirectAttributes redirectAttributes) {
    try {
        empruntService.retournerLivre(id, dateRetour);
        redirectAttributes.addFlashAttribute("success", "Livre retourné avec succès");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
    }
    return "redirect:/emprunt/liste";
}

    

    @GetMapping("/emprunt/retour-emprunt")
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
        model.addAttribute("empruntsSurPlace", empruntRepository.findActiveByTypeDeLecture("SUR_PLACE", LocalDateTime.now()));
        return "retour-emprunt"; 
    }

    @PostMapping("/emprunt/retour-emprunt")
    public String validerRetourSurPlace(@RequestParam Long empruntId, RedirectAttributes redirectAttributes) {
        try {
            empruntService.retournerLivre(empruntId, null);
            redirectAttributes.addFlashAttribute("success", "Retour validé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour: " + e.getMessage());
        }
        return "redirect:/emprunt/retour-emprunt";
    }

    @GetMapping("/emprunt/liste")
    public String afficherListeEmprunts(Model model, HttpSession session) {
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
        
        List<Emprunt> emprunts;
if (isAdmin) {
    emprunts = empruntRepository.findAllWithDetailsNotReturned();
} else {
    emprunts = empruntRepository.findByEmprunteurIdAndDateRetourEffectiveIsNull(userId);
}

        
         List<Map<String, Object>> empruntsFormatted = new ArrayList<>();
    for (Emprunt emprunt : emprunts) {
        Map<String, Object> empruntMap = new HashMap<>();
        empruntMap.put("livre", emprunt.getExemplaire().getLivre().getTitre());
        empruntMap.put("exemplaire", emprunt.getExemplaire().getNumExemplaire());
        empruntMap.put("emprunteur", emprunt.getEmprunteur().getUserName());
        empruntMap.put("dateDebut", Date.from(emprunt.getDateDebutEmprunt().atZone(ZoneId.systemDefault()).toInstant()));
        empruntMap.put("dateFin", emprunt.getDateFinEmprunt() != null ? 
            Date.from(emprunt.getDateFinEmprunt().atZone(ZoneId.systemDefault()).toInstant()) : null);
        empruntMap.put("type", emprunt.getTypeDeLecture());
        empruntMap.put("id", emprunt.getId());
        empruntsFormatted.add(empruntMap);
    }
    
    model.addAttribute("empruntsFormatted", empruntsFormatted);
    model.addAttribute("now", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    
    return "emprunt-list";
}
    

@GetMapping("/emprunt/prolongement")
public String afficherFormulaireProlongement(Model model, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return "redirect:/login";
    }

    Users user = usersRepository.findById(userId).orElse(null);
    if (user == null) {
        return "redirect:/login";
    }

    boolean isAdmin = "admin".equalsIgnoreCase(user.getProfilFormule().getProfil());
    if (!isAdmin) {
        return "redirect:/livres?error=notadmin";
    }

    List<Emprunt> empruntsActifs = empruntRepository.findActiveByTypeDeLecture("A_EMPORTER", LocalDateTime.now());

    for (Emprunt e : empruntsActifs) {
        if (e.getExemplaire() != null) {
            Hibernate.initialize(e.getExemplaire());
            if (e.getExemplaire().getLivre() != null) {
                Hibernate.initialize(e.getExemplaire().getLivre());
            }
        }
    }

    model.addAttribute("empruntsActifs", empruntsActifs);
    model.addAttribute("isAdmin", true);

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

        final int MAX_PROLONGEMENTS = 2; 

        try {
            Emprunt emprunt = empruntRepository.findById(empruntId)
                    .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
            if (emprunt.getDateFinEmprunt() == null || emprunt.getDateFinEmprunt().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Cet emprunt n'est plus actif");
                return "redirect:/emprunt/prolongement";
            }
            if (emprunt.getNombreProlongement() >= MAX_PROLONGEMENTS) {
                redirectAttributes.addFlashAttribute("error", "Le nombre maximum de prolongements a été atteint");
                return "redirect:/emprunt/prolongement";
            }

            LocalDateTime nouvelleDateFin = emprunt.getDateFinEmprunt().plusMonths(moisSupplementaires);
            emprunt.setDateFinEmprunt(nouvelleDateFin);
            emprunt.setDateFinProposee(nouvelleDateFin); 
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