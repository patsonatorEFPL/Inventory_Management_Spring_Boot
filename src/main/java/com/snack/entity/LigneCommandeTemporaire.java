package com.snack.entity;

import java.util.UUID;

public class LigneCommandeTemporaire {
    
    private String id;
    private Long articleId;
    private String articleNom;
    private float prixUnitaire;
    private int quantite;
    
    public LigneCommandeTemporaire() {
        this.id = UUID.randomUUID().toString();
    }
    
    public LigneCommandeTemporaire(Long articleId, String articleNom, float prixUnitaire, int quantite) {
        this.id = UUID.randomUUID().toString();
        this.articleId = articleId;
        this.articleNom = articleNom;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
    }
    
    // Créer à partir d'un article
    public static LigneCommandeTemporaire fromArticle(Article article, int quantite) {
        return new LigneCommandeTemporaire(
            article.getId(),
            article.getNom(),
            article.getPrix(),
            quantite
        );
    }
    
    // Convertir en LigneCommande réelle
    public LigneCommande toLigneCommande(Commande commande, Article article) {
        LigneCommande ligneCommande = new LigneCommande();
        ligneCommande.setCommande(commande);
        ligneCommande.setArticle(article);
        ligneCommande.setQuantite(this.quantite);
        return ligneCommande;
    }
    
    // Getters et Setters
    public String getId() {
        return id;
    }
    
    public Long getArticleId() {
        return articleId;
    }
    
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    
    public String getArticleNom() {
        return articleNom;
    }
    
    public void setArticleNom(String articleNom) {
        this.articleNom = articleNom;
    }
    
    public float getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(float prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
} 