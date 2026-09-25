package com.progetto.shared.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Risposta del server a un login riuscito.
 * Contiene il token JWT che il client dovrà inviare
 * nelle chiamate successive (header Authorization: Bearer ...).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenDTO {
    private String token;      // il JWT firmato
    private String username;   // eco dell'utente autenticato
    private Role role;         // ruolo dell'utente (USER / ANALYST)
}