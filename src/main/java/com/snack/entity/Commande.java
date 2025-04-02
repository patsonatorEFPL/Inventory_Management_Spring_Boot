package com.snack.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate dateCom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commande")
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    // Constructeurs
    public Commande() {
        this.dateCom = LocalDate.now();
        this.statut = StatutCommande.EN_COURS;
        this.dateCreation = new Date();
    }

    public Commande(LocalDate dateCom, StatutCommande statut, Client client) {
        this.dateCom = dateCom;
        this.statut = statut;
        this.client = client;
        this.dateCreation = new Date();
    }

    // Getters et Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDateCom() {
        return dateCom;
    }

    public void setDateCom(LocalDate dateCom) {
        this.dateCom = dateCom;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Méthode pour calculer le coût total
    public double getCoutTotal() {
        if (ligneCommandes == null || ligneCommandes.isEmpty()) {
            return 0.0;
        }
        return ligneCommandes.stream()
                .mapToDouble(ligne -> ligne.getQuantite() * ligne.getArticle().getPrix())
                .sum();
    }
}
