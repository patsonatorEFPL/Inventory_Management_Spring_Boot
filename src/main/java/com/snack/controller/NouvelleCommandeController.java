package com.snack.controller;

import com.snack.entity.Article;
import com.snack.entity.Client;
import com.snack.entity.Commande;
import com.snack.entity.CommandeTemporaire;
import com.snack.entity.LigneCommandeTemporaire;
import com.snack.repository.ArticleRepository;
import com.snack.repository.ClientRepository;
import com.snack.service.CommandeTemporaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/nouvelle-commande")
public class NouvelleCommandeController {

    @Autowired
    private CommandeTemporaireService commandeTemporaireService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Étape 1: Création d'une nouvelle commande temporaire
    @GetMapping("/commencer")
    public String commencerCommande(Model model) {
        CommandeTemporaire commandeTemp = commandeTemporaireService.creerCommandeTemporaire();
        model.addAttribute("commandeTemp", commandeTemp);
        model.addAttribute("articles", articleRepository.findAll());
        return "nouvelle_commande_etape1";
    }
    
    // Afficher la commande temporaire et permettre d'ajouter des articles
    @GetMapping("/{id}")
    public String afficherCommandeTemporaire(@PathVariable("id") String commandeTempId, Model model) {
        Optional<CommandeTemporaire> commandeTempOpt = commandeTemporaireService.getCommandeTemporaire(commandeTempId);
        
        if (commandeTempOpt.isEmpty()) {
            return "redirect:/commandes";
        }
        
        model.addAttribute("commandeTemp", commandeTempOpt.get());
        model.addAttribute("articles", articleRepository.findAll());
        return "nouvelle_commande_etape1";
    }
    
    // Ajouter un article à la commande temporaire
    @PostMapping("/{id}/ajouter-article")
    public String ajouterArticle(@PathVariable("id") String commandeTempId,
                                 @RequestParam("articleId") Long articleId,
                                 @RequestParam("quantite") int quantite,
                                 RedirectAttributes redirectAttributes) {
        
        if (quantite <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "La quantité doit être supérieure à 0");
            return "redirect:/nouvelle-commande/" + commandeTempId;
        }
        
        boolean success = commandeTemporaireService.ajouterArticle(commandeTempId, articleId, quantite);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible d'ajouter l'article (stock insuffisant ou article non trouvé)");
        }
        
        return "redirect:/nouvelle-commande/" + commandeTempId;
    }
    
    // Supprimer un article de la commande temporaire
    @PostMapping("/{id}/supprimer-article")
    public String supprimerArticle(@PathVariable("id") String commandeTempId,
                                  @RequestParam("ligneId") String ligneId) {
        
        commandeTemporaireService.supprimerArticle(commandeTempId, ligneId);
        return "redirect:/nouvelle-commande/" + commandeTempId;
    }
    
    // Mettre à jour la quantité d'un article
    @PostMapping("/{id}/mettre-a-jour-quantite")
    public String mettreAJourQuantite(@PathVariable("id") String commandeTempId,
                                     @RequestParam("ligneId") String ligneId,
                                     @RequestParam("quantite") int quantite,
                                     RedirectAttributes redirectAttributes) {
        
        if (quantite <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "La quantité doit être supérieure à 0");
            return "redirect:/nouvelle-commande/" + commandeTempId;
        }
        
        boolean success = commandeTemporaireService.mettreAJourQuantite(commandeTempId, ligneId, quantite);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de mettre à jour la quantité (stock insuffisant)");
        }
        
        return "redirect:/nouvelle-commande/" + commandeTempId;
    }
    
    // Étape 2: Choisir le client et finaliser la commande
    @GetMapping("/{id}/finaliser")
    public String finaliserCommande(@PathVariable("id") String commandeTempId, Model model) {
        Optional<CommandeTemporaire> commandeTempOpt = commandeTemporaireService.getCommandeTemporaire(commandeTempId);
        
        if (commandeTempOpt.isEmpty() || commandeTempOpt.get().getLignesCommande().isEmpty()) {
            return "redirect:/nouvelle-commande/" + commandeTempId;
        }
        
        model.addAttribute("commandeTemp", commandeTempOpt.get());
        model.addAttribute("clients", clientRepository.findAll());
        return "nouvelle_commande_etape2";
    }
    
    // Finaliser la commande avec le client choisi
    @PostMapping("/{id}/terminer")
    public String terminerCommande(@PathVariable("id") String commandeTempId,
                                  @RequestParam("clientId") Long clientId,
                                  RedirectAttributes redirectAttributes) {
        
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        
        if (clientOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Client invalide");
            return "redirect:/nouvelle-commande/" + commandeTempId + "/finaliser";
        }
        
        Commande commande = commandeTemporaireService.finaliserCommande(commandeTempId, clientOpt.get());
        
        if (commande == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de finaliser la commande");
            return "redirect:/nouvelle-commande/" + commandeTempId;
        }
        
        return "redirect:/commandes";
    }
    
    // Annuler la commande temporaire
    @GetMapping("/{id}/annuler")
    public String annulerCommande(@PathVariable("id") String commandeTempId) {
        commandeTemporaireService.annulerCommande(commandeTempId);
        return "redirect:/commandes";
    }
} 