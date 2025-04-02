package com.snack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotEmpty(message = "Le nom de l'article ne doit pas être vide.")
    private String nom;

    @Column(nullable = false)
    @Min(value = 0, message = "Le prix doit être supérieur ou égal à 0.")
    private float prix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeArticle type;

    @Column(nullable = false)
    @PositiveOrZero(message = "Le stock ne peut pas être négatif.")
    private int stock;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "article")
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    // Constructeurs
    public Article() {
    }

    public Article(String nom, float prix, TypeArticle type, int stock) {
        this.nom = nom;
        this.prix = prix;
        this.type = type;
        this.stock = stock;
    }

    // Getters et Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public TypeArticle getType() {
        return type;
    }

    public void setType(TypeArticle type) {
        this.type = type;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }
}
