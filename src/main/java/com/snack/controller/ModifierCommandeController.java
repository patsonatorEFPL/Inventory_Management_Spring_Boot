package com.snack.controller;

import com.snack.entity.Article;
import com.snack.entity.CommandeTemporaire;
import com.snack.repository.ArticleRepository;
import com.snack.service.CommandeTemporaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/modifier-commande")
public class ModifierCommandeController {

    @Autowired
    private CommandeTemporaireService commandeTemporaireService;

    @Autowired
    private ArticleRepository articleRepository;

    // Commencer la modification d'une commande existante
    @GetMapping("/{commandeId}")
    public String commencerModification(@PathVariable("commandeId") Long commandeId, Model model) {
        try {
            CommandeTemporaire commandeTemp = commandeTemporaireService.creerCommandeTemporaireDepuisCommande(commandeId);
            model.addAttribute("commandeTemp", commandeTemp);
            model.addAttribute("articles", articleRepository.findAll());
            return "modifier_commande_articles";
        } catch (IllegalArgumentException e) {
            return "redirect:/commandes";
        }
    }
    
    // Afficher la commande temporaire et permettre d'ajouter des articles
    @GetMapping("/articles/{id}")
    public String afficherCommandeTemporaire(@PathVariable("id") String commandeTempId, Model model) {
        Optional<CommandeTemporaire> commandeTempOpt = commandeTemporaireService.getCommandeTemporaire(commandeTempId);
        
        if (commandeTempOpt.isEmpty() || !commandeTempOpt.get().estModification()) {
            return "redirect:/commandes";
        }
        
        model.addAttribute("commandeTemp", commandeTempOpt.get());
        model.addAttribute("articles", articleRepository.findAll());
        return "modifier_commande_articles";
    }
    
    // Ajouter un article à la commande temporaire
    @PostMapping("/articles/{id}/ajouter-article")
    public String ajouterArticle(@PathVariable("id") String commandeTempId,
                                 @RequestParam("articleId") Long articleId,
                                 @RequestParam("quantite") int quantite,
                                 RedirectAttributes redirectAttributes) {
        
        if (quantite <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "La quantité doit être supérieure à 0");
            return "redirect:/modifier-commande/articles/" + commandeTempId;
        }
        
        boolean success = commandeTemporaireService.ajouterArticle(commandeTempId, articleId, quantite);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible d'ajouter l'article (stock insuffisant ou article non trouvé)");
        }
        
        return "redirect:/modifier-commande/articles/" + commandeTempId;
    }
    
    // Supprimer un article de la commande temporaire
    @PostMapping("/articles/{id}/supprimer-article")
    public String supprimerArticle(@PathVariable("id") String commandeTempId,
                                  @RequestParam("ligneId") String ligneId) {
        
        commandeTemporaireService.supprimerArticle(commandeTempId, ligneId);
        return "redirect:/modifier-commande/articles/" + commandeTempId;
    }
    
    // Mettre à jour la quantité d'un article
    @PostMapping("/articles/{id}/mettre-a-jour-quantite")
    public String mettreAJourQuantite(@PathVariable("id") String commandeTempId,
                                     @RequestParam("ligneId") String ligneId,
                                     @RequestParam("quantite") int quantite,
                                     RedirectAttributes redirectAttributes) {
        
        if (quantite <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "La quantité doit être supérieure à 0");
            return "redirect:/modifier-commande/articles/" + commandeTempId;
        }
        
        boolean success = commandeTemporaireService.mettreAJourQuantite(commandeTempId, ligneId, quantite);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de mettre à jour la quantité (stock insuffisant)");
        }
        
        return "redirect:/modifier-commande/articles/" + commandeTempId;
    }
    
    // Finaliser la modification de la commande
    @GetMapping("/finaliser/{id}")
    public String finaliserModification(@PathVariable("id") String commandeTempId, RedirectAttributes redirectAttributes) {
        try {
            commandeTemporaireService.mettreAJourCommande(commandeTempId);
            redirectAttributes.addFlashAttribute("successMessage", "Commande mise à jour avec succès");
            return "redirect:/commandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour de la commande: " + e.getMessage());
            return "redirect:/modifier-commande/articles/" + commandeTempId;
        }
    }
    
    // Annuler la modification
    @GetMapping("/annuler/{id}")
    public String annulerModification(@PathVariable("id") String commandeTempId) {
        commandeTemporaireService.annulerCommande(commandeTempId);
        return "redirect:/commandes";
    }
} 