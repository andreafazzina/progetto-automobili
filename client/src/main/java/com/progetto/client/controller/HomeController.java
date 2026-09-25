package com.progetto.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.progetto.client.session.SessionState;
import com.progetto.shared.service.AutoService;

@Controller
public class HomeController {

    private final AutoService autoService;   // il proxy
    private final SessionState session;

    public HomeController(AutoService autoService, SessionState session) {
        this.autoService = autoService;
        this.session = session;
    }

    @GetMapping("/")
    public String home(Model model) {

        // Verifica sull'utente
        if (!session.isAuthenticated()) return "redirect:/login";
        if (session.isAnalyst()) return "redirect:/analysis";

        // venditore: catalogo brand
        model.addAttribute("username", session.getUsername());
        model.addAttribute("brands", autoService.getBrands());
        return "home";
    }
}