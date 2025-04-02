package com.snack.controller;

import com.snack.entity.Client;
import com.snack.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // Liste des clients
    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("listClients", clientRepository.findAll());
        return "clients";
    }

    // Formulaire pour ajouter un nouveau client
    @GetMapping("/new")
    public String showNewClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "new_client";
    }

    // Sauvegarder un nouveau client avec validation de l'unicité de l'email
    @PostMapping("/saveNew")
    public String saveNewClient(@Valid @ModelAttribute Client client, Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "new_client";
        }
        List<Client> existingClients = clientRepository.findByEmail(client.getEmail());
        if (!existingClients.isEmpty()) {
            model.addAttribute("message", "Un client avec cet email existe déjà !");
            return "new_client";
        }
        clientRepository.save(client);
        return "redirect:/clients";
    }

    // Formulaire pour mettre à jour un client
    @GetMapping("/update")
    public String showUpdateClientForm(@RequestParam("id") Long id, Model model) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client non trouvé pour l'id : " + id));
        model.addAttribute("client", client);
        return "update_client";
    }

    // Sauvegarder les modifications d'un client avec validation de l'unicité de l'email
    @PostMapping("/saveUpdate")
public String saveUpdateClient(@Valid @ModelAttribute Client clientForm, Errors errors, Model model) {
    if (errors.hasErrors()) {
        return "update_client";
    }

    Client existingClient = clientRepository.findById(clientForm.getId())
        .orElseThrow(() -> new RuntimeException("Client non trouvé pour l'id : " + clientForm.getId()));

    List<Client> existingClientsByEmail = clientRepository.findByEmail(clientForm.getEmail());
    if (!existingClientsByEmail.isEmpty() && !existingClientsByEmail.get(0).getId().equals(clientForm.getId())) {
        model.addAttribute("message", "Un autre client avec cet email existe déjà !");
        return "update_client";
    }

    // Mettre à jour les données du client existant
    existingClient.setNom(clientForm.getNom());
    existingClient.setPrenom(clientForm.getPrenom());
    existingClient.setEmail(clientForm.getEmail());
    
    clientRepository.save(existingClient);

    return "redirect:/clients";
}

    
    @GetMapping("/filter")
public String filterClients(@RequestParam(name = "email", required = false) String email, Model model) {
    List<Client> clients;
    if (email != null && !email.isEmpty()) {
        clients = clientRepository.findByEmailContaining(email);
    } else {
        clients = clientRepository.findAll();
    }
    model.addAttribute("listClients", clients);
    model.addAttribute("filterEmail", email);
    return "clients";
}

    // Supprimer un client
    @GetMapping("/delete")
    public String deleteClient(@RequestParam("id") Long id) {
        clientRepository.deleteById(id);
        return "redirect:/clients";
    }
}
