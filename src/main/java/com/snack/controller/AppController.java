/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/")
    public String viewHomePage() {
        // Retourne le nom de la vue, par exemple une page d'accueil ou un tableau de bord
        return "dashboard"; // Redirige vers le template Thymeleaf "dashboard.html"
    }
}
