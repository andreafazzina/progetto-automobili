package com.progetto.server.security;

import com.progetto.shared.dto.auth.Role;

/**
 * Contenitore del "soggetto" autenticato per la richiesta HTTP in corso. 
 * Il filtro JWT vi deposita username e ruolo dopo aver verificato il token; 
 * i service li leggono per decidere le autorizzazioni.
 *
 * Usa ThreadLocal: ogni richiesta HTTP gira su un thread separato, quindi
 * ogni richiesta ha il proprio contesto isolato dalle altre (fondamentale
 * con richieste concorrenti).
 */
public final class CurrentUserContext {

    /** I dati dell'utente autenticato per la richiesta corrente. */
    public record AuthenticatedUser(String username, Role role) {}

    // Un valore distinto per ciascun thread (quindi per ciascuna richiesta).
    private static final ThreadLocal<AuthenticatedUser> CONTEXT = new ThreadLocal<>();

    private CurrentUserContext() { }   

    /** Imposta l'utente autenticato (chiamato dal filtro dopo la verifica). */
    public static void set(String username, Role role) {
        CONTEXT.set(new AuthenticatedUser(username, role));
    }

    /** Restituisce l'utente corrente, o null se la richiesta non è autenticata. */
    public static AuthenticatedUser get() {
        return CONTEXT.get();
    }

    /** Ruolo corrente, o null se non autenticato (comodità per i service). */
    public static Role getRole() {
        AuthenticatedUser u = CONTEXT.get();
        return (u != null) ? u.role() : null;
    }

    /** Vero se la richiesta corrente è autenticata. */
    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    /**
     * Pulisce il contesto a fine richiesta. CRUCIALE: i thread vengono
     * riutilizzati dal server per richieste successive; senza clear(), la
     * richiesta seguente erediterebbe il ruolo della precedente.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}