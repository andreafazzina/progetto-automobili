package com.progetto.server.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.progetto.shared.dto.auth.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reference Monitor: intercetta TUTTE le richieste HTTP prima che
 * raggiungano il controller (il ProtectionObject). 
 * Verifica il token JWT e, se valido, deposita l'identità nel CurrentUserContext. 
 * Le richieste senza token valido proseguono come "non autenticate": saranno i service a negare l'accesso dove serve.
 *
 * Il login è pubblico (nessun token richiesto) e viene lasciato passare.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/login";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {
            try {
                // Prova ad autenticare, se è presente un token valido
                String header = request.getHeader(AUTH_HEADER);
                
                if (header != null && header.startsWith(BEARER_PREFIX)) {
                    String token = header.substring(BEARER_PREFIX.length());
                    try {
                        DecodedJWT decoded = jwtService.verify(token);   // lancia se non valido
                        String username = jwtService.getUsername(decoded);
                        Role role = jwtService.getRole(decoded);
                        CurrentUserContext.set(username, role);          // popola il Subject
                    } catch (Exception ex) {
                        // Token presente ma non valido/scaduto: NON autenticato.
                        // Non blocchiamo qui; il contesto resta vuoto e i
                        // service negheranno l'accesso dove richiesto.
                        CurrentUserContext.clear();
                    }
                }

                // Inoltra la richiesta lungo la catena (verso il controller)
                chain.doFilter(request, response);

            } finally {
                // CRUCIALE: pulisci sempre, anche in caso di eccezione.
                // I thread sono riutilizzati: senza clear, la richiesta
                // successiva erediterebbe questa identità.
                CurrentUserContext.clear();
        }
    }

    /**
     * Il login non passa dal controllo del token: è l'endpoint pubblico
     * con cui si OTTIENE il token. Escludendolo, evitiamo il paradosso
     * "serve un token per ottenere un token".
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals(LOGIN_PATH);
    }
}