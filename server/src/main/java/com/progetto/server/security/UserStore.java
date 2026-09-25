package com.progetto.server.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.progetto.shared.dto.auth.Role;

/**
 * Fonte degli utenti (AuthenticationInfo del pattern Authenticator).
 */
@Component
public class UserStore {

    // Encoder scrypt: memory-hard
    private final PasswordEncoder encoder = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();

    /** Dati di un utente registrato. */
    public record User(String username, String passwordHash, Role role) {}
    /** Mappa username -> dati utente. */
    private final Map<String, User> users = new HashMap<>();

    public UserStore(
            @Value("${app.users.seller.hash}") String sellerHash,
            @Value("${app.users.analyst.hash}") String analystHash) {
        
        users.put("seller", new User("seller", sellerHash, Role.SELLER));
        users.put("analyst", new User("analyst", analystHash, Role.ANALYST));
    }

    /**
     * Verifica le credenziali (ruolo di Authenticator.verify()).
     * Confronta la password ricevuta con l'hash conservato.
     * Restituisce l'utente se combaciano, altrimenti Optional vuoto.
     * L'esito NON distingue "username assente" da "password errata":
     * un unico fallimento previene la user enumeration.
     */
    public Optional<User> authenticate(String username, String rawPassword) {
        User u = users.get(username);
        if (u != null && encoder.matches(rawPassword, u.passwordHash())) {
            return Optional.of(u);
        }
        return Optional.empty();
    }
}