package com.snack.controller;

import com.snack.entity.LigneCommande;
import com.snack.repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lignescommande")
public class LigneCommandeController {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @GetMapping
    public String listLignesCommande(Model model) {
        model.addAttribute("listLignesCommande", ligneCommandeRepository.findAll());
        return "lignes_commande";
    }

    @GetMapping("/filter")
    public String filterLignesCommandeByCommande(@RequestParam(name = "commandeId", required = false) Long commandeId, Model model) {
        if (commandeId != null) {
            List<LigneCommande> lignesCommande = ligneCommandeRepository.findByCommandeId(commandeId);
            model.addAttribute("listLignesCommande", lignesCommande);
            model.addAttribute("filterCommandeId", commandeId);
        } else {
            model.addAttribute("listLignesCommande", ligneCommandeRepository.findAll());
        }
        return "lignes_commande";
    }
}
