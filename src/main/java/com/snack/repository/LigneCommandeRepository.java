package com.snack.repository;

import com.snack.entity.LigneCommande;
import com.snack.entity.Commande;
import com.snack.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);
    Optional<LigneCommande> findByCommandeAndArticle(Commande commande, Article article);
}
