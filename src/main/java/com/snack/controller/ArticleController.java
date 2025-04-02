package com.snack.controller;

import com.snack.entity.Article;
import com.snack.entity.TypeArticle;
import com.snack.repository.ArticleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public String listArticles(Model model) {
        model.addAttribute("listArticles", articleRepository.findAll());
        model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); // Pour afficher les types
        return "articles";
    }

    @GetMapping("/new")
    public String showNewArticleForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); // Pour sélectionner un type
        return "new_article";
    }

    @PostMapping("/saveNew")
    public String saveNewArticle(@Valid @ModelAttribute Article article, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); // Recharge les types en cas d'erreur
            return "new_article";
        }
        articleRepository.save(article);
        return "redirect:/articles";
    }

    @GetMapping("/update")
    public String showUpdateArticleForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("article", articleRepository.findById(id).orElseThrow());
        model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); // Pour sélectionner un type
        return "update_article";
    }

    @PostMapping("/saveUpdate")
    public String saveUpdateArticle(@Valid @ModelAttribute Article article, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); // Recharge les types en cas d'erreur
            return "update_article";
        }
        articleRepository.save(article);
        return "redirect:/articles";
    }

    @GetMapping("/delete")
    public String deleteArticle(@RequestParam("id") Long id) {
        articleRepository.deleteById(id);
        return "redirect:/articles";
    }

    @GetMapping("/filter")
public String filterArticles(@RequestParam(name = "type", required = false) String type, Model model) {
    List<Article> articles;
    if (type != null && !type.isEmpty()) {
        try {
            TypeArticle enumType = TypeArticle.valueOf(type); 
            articles = articleRepository.findByType(enumType);
            model.addAttribute("selectedType", type);
        } catch (IllegalArgumentException e) {
            // Si le type fourni n'est pas valide, renvoyer tous les articles
            articles = articleRepository.findAll();
            model.addAttribute("message", "Type invalide, affichage de tous les articles.");
        }
    } else {
        articles = articleRepository.findAll();
    }
    model.addAttribute("listArticles", articles);
    model.addAttribute("listTypes", Arrays.asList(TypeArticle.values())); 
    return "articles";
}

    
}
