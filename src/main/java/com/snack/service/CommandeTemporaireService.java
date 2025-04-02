package com.snack.service;

import com.snack.entity.*;
import com.snack.repository.ArticleRepository;
import com.snack.repository.ClientRepository;
import com.snack.repository.CommandeRepository;
import com.snack.repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class CommandeTemporaireService {

    private final Map<String, CommandeTemporaire> commandesTemporaires = new HashMap<>();
    
    @Autowired
    private CommandeRepository commandeRepository;
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    // Créer une nouvelle commande temporaire
    public CommandeTemporaire creerCommandeTemporaire() {
        CommandeTemporaire commandeTemp = new CommandeTemporaire();
        commandesTemporaires.put(commandeTemp.getId(), commandeTemp);
        return commandeTemp;
    }
    
    // Récupérer une commande temporaire par son ID
    public Optional<CommandeTemporaire> getCommandeTemporaire(String id) {
        return Optional.ofNullable(commandesTemporaires.get(id));
    }
    
    // Ajouter un article à une commande temporaire
    public boolean ajouterArticle(String commandeTempId, Long articleId, int quantite) {
        CommandeTemporaire commandeTemp = commandesTemporaires.get(commandeTempId);
        if (commandeTemp == null) {
            return false;
        }
        
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty() || articleOpt.get().getStock() < quantite) {
            return false;
        }
        
        Article article = articleOpt.get();
        
        // Vérifier si l'article existe déjà dans la commande
        Optional<LigneCommandeTemporaire> ligneExistante = commandeTemp.getLignesCommande().stream()
                .filter(ligne -> ligne.getArticleId().equals(articleId))
                .findFirst();
        
        if (ligneExistante.isPresent()) {
            // Mettre à jour la quantité existante
            LigneCommandeTemporaire ligne = ligneExistante.get();
            int nouvelleQuantite = ligne.getQuantite() + quantite;
            
            if (article.getStock() < nouvelleQuantite) {
                return false;
            }
            
            ligne.setQuantite(nouvelleQuantite);
        } else {
            // Ajouter un nouvel article
            commandeTemp.addLigneCommande(LigneCommandeTemporaire.fromArticle(article, quantite));
        }
        
        return true;
    }
    
    // Supprimer un article d'une commande temporaire
    public boolean supprimerArticle(String commandeTempId, String ligneId) {
        CommandeTemporaire commandeTemp = commandesTemporaires.get(commandeTempId);
        if (commandeTemp == null) {
            return false;
        }
        
        commandeTemp.removeLigneCommande(ligneId);
        return true;
    }
    
    // Mettre à jour la quantité d'un article
    public boolean mettreAJourQuantite(String commandeTempId, String ligneId, int nouvelleQuantite) {
        CommandeTemporaire commandeTemp = commandesTemporaires.get(commandeTempId);
        if (commandeTemp == null) {
            return false;
        }
        
        Optional<LigneCommandeTemporaire> ligneOpt = commandeTemp.getLignesCommande().stream()
                .filter(ligne -> ligne.getId().equals(ligneId))
                .findFirst();
        
        if (ligneOpt.isEmpty()) {
            return false;
        }
        
        LigneCommandeTemporaire ligne = ligneOpt.get();
        Optional<Article> articleOpt = articleRepository.findById(ligne.getArticleId());
        
        if (articleOpt.isEmpty() || articleOpt.get().getStock() < nouvelleQuantite) {
            return false;
        }
        
        ligne.setQuantite(nouvelleQuantite);
        return true;
    }
    
    // Finaliser la commande en ajoutant le client
    @Transactional
    public Commande finaliserCommande(String commandeTempId, Client client) {
        CommandeTemporaire commandeTemp = commandesTemporaires.get(commandeTempId);
        if (commandeTemp == null || commandeTemp.getLignesCommande().isEmpty()) {
            return null;
        }
        
        // Créer la commande finale
        Commande commande = commandeTemp.toCommande(client);
        commande = commandeRepository.save(commande);
        
        // Créer les lignes de commande et mettre à jour le stock
        for (LigneCommandeTemporaire ligneTemp : commandeTemp.getLignesCommande()) {
            Article article = articleRepository.findById(ligneTemp.getArticleId()).orElse(null);
            if (article != null) {
                LigneCommande ligneCommande = ligneTemp.toLigneCommande(commande, article);
                ligneCommandeRepository.save(ligneCommande);
                
                // Mettre à jour le stock
                article.setStock(article.getStock() - ligneTemp.getQuantite());
                articleRepository.save(article);
            }
        }
        
        // Supprimer la commande temporaire
        commandesTemporaires.remove(commandeTempId);
        
        return commande;
    }
    
    // Annuler une commande temporaire
    public void annulerCommande(String commandeTempId) {
        commandesTemporaires.remove(commandeTempId);
    }
    
    // Créer une commande temporaire à partir d'une commande existante
    public CommandeTemporaire creerCommandeTemporaireDepuisCommande(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
        
        CommandeTemporaire commandeTemp = new CommandeTemporaire();
        commandeTemp.setDateCom(commande.getDateCom());
        
        // Copier les lignes de commande existantes
        for (LigneCommande ligne : commande.getLigneCommandes()) {
            Article article = ligne.getArticle();
            LigneCommandeTemporaire ligneTemp = new LigneCommandeTemporaire(
                article.getId(),
                article.getNom(),
                article.getPrix(),
                ligne.getQuantite()
            );
            commandeTemp.addLigneCommande(ligneTemp);
        }
        
        // Stocker l'ID de la commande originale pour pouvoir la mettre à jour plus tard
        commandeTemp.setCommandeOriginaleId(commandeId);
        commandeTemp.setClientId(commande.getClient().getId());
        
        commandesTemporaires.put(commandeTemp.getId(), commandeTemp);
        return commandeTemp;
    }
    
    // Mettre à jour une commande existante
    @Transactional
    public Commande mettreAJourCommande(String commandeTempId) {
        CommandeTemporaire commandeTemp = commandesTemporaires.get(commandeTempId);
        if (commandeTemp == null || commandeTemp.getCommandeOriginaleId() == null || commandeTemp.getLignesCommande().isEmpty()) {
            return null;
        }
        
        Long commandeId = commandeTemp.getCommandeOriginaleId();
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande originale non trouvée"));
        
        // Récupérer le client si besoin
        if (commandeTemp.getClientId() != null && (commande.getClient() == null || !commande.getClient().getId().equals(commandeTemp.getClientId()))) {
            Client client = clientRepository.findById(commandeTemp.getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé"));
            commande.setClient(client);
        }
        
        // Supprimer les anciennes lignes de commande et restaurer les stocks
        List<LigneCommande> anciennesLignes = ligneCommandeRepository.findByCommandeId(commandeId);
        for (LigneCommande ancienneLigne : anciennesLignes) {
            Article article = ancienneLigne.getArticle();
            article.setStock(article.getStock() + ancienneLigne.getQuantite());
            articleRepository.save(article);
            ligneCommandeRepository.delete(ancienneLigne);
        }
        
        // Créer les nouvelles lignes de commande et mettre à jour le stock
        for (LigneCommandeTemporaire ligneTemp : commandeTemp.getLignesCommande()) {
            Article article = articleRepository.findById(ligneTemp.getArticleId()).orElse(null);
            if (article != null) {
                LigneCommande ligneCommande = ligneTemp.toLigneCommande(commande, article);
                ligneCommandeRepository.save(ligneCommande);
                
                // Mettre à jour le stock
                article.setStock(article.getStock() - ligneTemp.getQuantite());
                articleRepository.save(article);
            }
        }
        
        // Mettre à jour la commande
        commande = commandeRepository.save(commande);
        
        // Supprimer la commande temporaire
        commandesTemporaires.remove(commandeTempId);
        
        return commande;
    }
} 