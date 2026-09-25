package com.progetto.server.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.progetto.shared.dto.auth.Role;

/**
 * Genera e verifica i token JWT (libreria auth0 java-jwt).
 * Il token trasporta lo username (subject) e il ruolo (claim custom),
 * firmato con chiave segreta HMAC-SHA256: chiunque può leggerne il
 * contenuto, ma solo chi ha la chiave può crearne uno valido.
 */
@Component
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long expirationMs;

    // I valori arrivano da application.properties (o da variabile d'ambiente)
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        this.algorithm = Algorithm.HMAC256(secret);   // firma simmetrica
        this.issuer = issuer;
        this.expirationMs = expirationMs;

        // Verificatore riutilizzabile: controlla firma + issuer atteso
        this.verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    /**
     * Genera un token firmato per l'utente indicato.
     * Ruolo di "produzione della proof" del pattern Authenticator.
     */
    public String generateToken(String username, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withIssuer(issuer)                // iss: chi ha emesso
                .withSubject(username)             // sub: identità utente
                .withClaim("role", role.name())    // claim custom: il ruolo
                .withIssuedAt(now)                 // iat: quando emesso
                .withExpiresAt(expiry)             // exp: quando scade
                .sign(algorithm);                  // firma crittografica
    }

    /**
     * Verifica il token
     * Lancia un'eccezione (JWTVerificationException) se non valido.
     */
    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }

    /** Estrae lo username dal token già verificato. */
    public String getUsername(DecodedJWT decoded) {
        return decoded.getSubject();
    }

    /** Estrae il ruolo dal token già verificato. */
    public Role getRole(DecodedJWT decoded) {
        return Role.valueOf(decoded.getClaim("role").asString());
    }
}