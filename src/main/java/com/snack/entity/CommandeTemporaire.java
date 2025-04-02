package com.snack.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommandeTemporaire {
    
    private String id;
    private LocalDate dateCom;
    private List<LigneCommandeTemporaire> lignesCommande;
    private Date dateCreation;
    private Long commandeOriginaleId; // ID de la commande originale pour la modification
    private Long clientId; // ID du client associé
    
    public CommandeTemporaire() {
        this.id = UUID.randomUUID().toString();
        this.dateCom = LocalDate.now();
        this.lignesCommande = new ArrayList<>();
        this.dateCreation = new Date();
    }
    
    public String getId() {
        return id;
    }
    
    public LocalDate getDateCom() {
        return dateCom;
    }
    
    public void setDateCom(LocalDate dateCom) {
        this.dateCom = dateCom;
    }
    
    public List<LigneCommandeTemporaire> getLignesCommande() {
        return lignesCommande;
    }
    
    public void addLigneCommande(LigneCommandeTemporaire ligne) {
        this.lignesCommande.add(ligne);
    }
    
    public void removeLigneCommande(String ligneId) {
        this.lignesCommande.removeIf(ligne -> ligne.getId().equals(ligneId));
    }
    
    public Date getDateCreation() {
        return dateCreation;
    }
    
    public double getCoutTotal() {
        return lignesCommande.stream()
                .mapToDouble(ligne -> ligne.getPrixUnitaire() * ligne.getQuantite())
                .sum();
    }
    
    // Getters et setters pour les nouveaux champs
    public Long getCommandeOriginaleId() {
        return commandeOriginaleId;
    }
    
    public void setCommandeOriginaleId(Long commandeOriginaleId) {
        this.commandeOriginaleId = commandeOriginaleId;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    // Déterminer si c'est une modification ou une nouvelle commande
    public boolean estModification() {
        return commandeOriginaleId != null;
    }
    
    // Convertir en commande réelle
    public Commande toCommande(Client client) {
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setDateCom(this.dateCom);
        commande.setStatut(StatutCommande.EN_COURS);
        commande.setDateCreation(this.dateCreation);
        return commande;
    }
} 