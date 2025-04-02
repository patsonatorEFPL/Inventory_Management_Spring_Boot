package com.snack.controller;

import com.snack.entity.Commande;
import com.snack.entity.StatutCommande;
import com.snack.repository.CommandeRepository;
import com.snack.repository.ClientRepository;
import com.snack.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/commandes")
public class CommandeController {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ArticleRepository articleRepository;

    // Liste des commandes
    @GetMapping
    public String listCommandes(Model model) {
        model.addAttribute("listCommandes", commandeRepository.findAll());
        model.addAttribute("statuts", StatutCommande.values());
        return "commandes";
    }

    // Supprimer une commande
    @GetMapping("/delete")
    public String deleteCommande(@RequestParam("id") Long id) {
        commandeRepository.deleteById(id);
        return "redirect:/commandes";
    }

    // Mettre à jour le statut d'une commande
    @PostMapping("/updateStatut")
    public String updateStatut(@RequestParam Long id, @RequestParam StatutCommande statut) {
        Optional<Commande> commandeOpt = commandeRepository.findById(id);
        if (commandeOpt.isPresent()) {
            Commande commande = commandeOpt.get();
            commande.setStatut(statut);
            commandeRepository.save(commande);
        }
        return "redirect:/commandes";
    }
    
    // Filtrer les commandes par statut
    @GetMapping("/filter")
    public String filterCommandesByStatut(@RequestParam(name = "statut", required = false) StatutCommande statut, Model model) {
        if (statut != null) {
            model.addAttribute("listCommandes", commandeRepository.findByStatut(statut));
            model.addAttribute("selectedStatut", statut);
        } else {
            model.addAttribute("listCommandes", commandeRepository.findAll());
        }
        model.addAttribute("statuts", StatutCommande.values());
        return "commandes";
    }
}
