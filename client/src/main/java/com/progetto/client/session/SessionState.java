package com.progetto.client.session;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.progetto.shared.dto.auth.Role;

/**
 * Client Session State
 * Conserva il token JWT e il ruolo ottenuti al login, così che
 * le richieste successive possano essere autenticate senza
 * richiedere di nuovo le credenziali.
 *
 * @SessionScope: esiste un'istanza distinta PER OGNI sessione HTTP
 * (per ogni utente/browser). Utenti diversi non condividono il token.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionState {

    private String token;
    private String username;
    private Role role;

    /** Salva i dati ottenuti da un login riuscito. */
    public void authenticate(String token, String username, Role role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    /** Azzera la sessione (logout). */
    public void clear() {
        this.token = null;
        this.username = null;
        this.role = null;
    }

    public boolean isAuthenticated() {
        return token != null;
    }

    public boolean isAnalyst() {
        return role == Role.ANALYST;
    }

    public boolean isSeller() {
        return role == Role.SELLER; 
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}