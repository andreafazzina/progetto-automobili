package com.progetto.shared.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Credenziali inviate dal client al server per il login.
 * È l'input dell'operazione AutoService.login().
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {
    private String username;
    private String password;
}