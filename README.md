# Snack & Inventory Management — Spring Boot

Application web MVC complète de gestion commerciale et d'inventaire développée avec **Spring Boot 3**, **Spring Data JPA**, **Thymeleaf** et **MySQL**.

---

## Stack technique

| Composant | Technologie |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.4.1 (Spring MVC, Spring Data JPA, Hibernate, Bean Validation) |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5 (CDN) |
| **Base de données** | MySQL 8.x |
| **Build & Outils** | Maven 3.9+, Spring Boot DevTools, Spring Boot Actuator |

---

## Fonctionnalités

- **Gestion des Articles / Catalogue :**
  - Ajout, consultation, mise à jour et suppression d'articles.
  - Catégorisation par types d'articles (`TypeArticle`) et gestion des tarifs unitaires.
- **Gestion des Clients :**
  - CRUD complet des fiches clients (coordonnées, historique).
  - Recherche rapide et filtrage de clients.
- **Tunnel de Commande (Workflow multi-étapes) :**
  - Création d'une commande en 2 étapes avec panier temporaire (`CommandeTemporaireService`).
  - Ajout et ajustement des lignes de commande avec calcul automatique des totaux.
  - Modification, annulation et mise à jour des statuts de commande (`StatutCommande`).
- **Dashboard :**
  - Vue d'ensemble de l'activité, liste des commandes en cours et accès rapide aux modules.

---

## Architecture logicielle

L'application respecte les principes de l'architecture MVC standard et la séparation en couches :

```text
src/main/java/com/snack/
├── SnackApplication.java         # Point d'entrée Spring Boot
├── controller/                   # Couche Contrôleurs Spring MVC
│   ├── AppController.java        # Dashboard et navigation principale
│   ├── ArticleController.java    # Gestion du catalogue d'articles
│   ├── ClientController.java     # Gestion des clients et recherche
│   ├── CommandeController.java   # Consultation et cycle de vie des commandes
│   ├── NouvelleCommandeController.java # Workflow de prise de commande (étapes 1 & 2)
│   └── ModifierCommandeController.java # Édition des lignes de commande
├── service/                      # Couche Métier
│   └── CommandeTemporaireService.java
├── repository/                   # Couche Persistance (Spring Data JPA)
│   ├── ArticleRepository.java
│   ├── ClientRepository.java
│   ├── CommandeRepository.java
│   └── LigneCommandeRepository.java
└── entity/                       # Modèle de données JPA / Hibernate
    ├── Article.java
    ├── Client.java
    ├── Commande.java
    ├── LigneCommande.java
    ├── CommandeTemporaire.java
    └── StatutCommande.java
```

---

## Modèle de données

- **Client** `(1) ────── (0..*)` **Commande**
- **Commande** `(1) ────── (1..*)` **LigneCommande**
- **Article** `(1) ────── (0..*)` **LigneCommande**

Les relations sont mappées via JPA avec gestion des cascades et intégrité référentielle assurée par Hibernate.

---

## Installation et démarrage

### 1. Prérequis
- **JDK 21** ou supérieur
- **Maven 3.9+**
- Instance **MySQL** locale en cours d'exécution

### 2. Base de données
Créer la base de données MySQL :
```sql
CREATE DATABASE snack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configuration
Ajuster le fichier `src/main/resources/application.properties` selon votre environnement local :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/snack_db?autoReconnect=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Lancement
```bash
# Compilation et démarrage
./mvnw spring-boot:run
# Ou sur Windows PowerShell :
mvn spring-boot:run
```

L'application est disponible sur : **`http://localhost:8080`**
