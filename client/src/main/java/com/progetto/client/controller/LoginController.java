package com.progetto.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.progetto.client.session.SessionState;
import com.progetto.shared.dto.auth.CredentialsDTO;
import com.progetto.shared.service.AutoService;

/**
 * Controller MVC per login e logout.
 * A differenza del controller REST del server (@RestController),
 * questo è un @Controller "classico": i suoi metodi restituiscono
 * il NOME di una pagina Thymeleaf da mostrare, non JSON.
 * Chiama il proxy (AutoService) senza sapere nulla di HTTP.
 */
@Controller
public class LoginController {

    private final AutoService autoService;   // il proxy, iniettato da Spring
    private final SessionState session;

    public LoginController(AutoService autoService, SessionState session) {
        this.autoService = autoService;
        this.session = session;
    }

    /** GET /login → mostra il form di accesso. */
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("credentials", new CredentialsDTO());
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute CredentialsDTO credentials, Model model) {
        try {
            // Tenta il login tramite il proxy
            autoService.login(credentials);
            return "redirect:/"; // Se va a buon fine, vai alla home
            
        } catch (Exception ex) {
            // Se il server restituisce errore, il RestClient lancia un'eccezione.
            // La catturiamo e rimandiamo l'utente alla vista del login.
            model.addAttribute("error", "Username o password errata");
            model.addAttribute("credentials", new CredentialsDTO()); // Ripopola il form vuoto
            return "login"; // Ritorna il nome del template HTML, non un redirect!
        }
    }

    /** GET /logout → azzera la sessione e torna al login. */
    @GetMapping("/logout")
    public String logout() {
        session.clear();
        return "redirect:/login";
    }
}