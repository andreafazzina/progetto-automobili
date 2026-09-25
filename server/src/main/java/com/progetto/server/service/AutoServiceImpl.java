package com.progetto.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.progetto.shared.dto.analysis.*;
import com.progetto.shared.dto.catalog.*;
import com.progetto.shared.dto.batch.*;
import com.progetto.shared.dto.auth.*;
import com.progetto.server.security.*;

import com.progetto.shared.service.AutoService;


/**
 * Implementazione del Remote Facade lato server.
 */
@Service
public class AutoServiceImpl implements AutoService {

    private final CatalogService catalogService;
    private final AnalysisService analysisService;
    private final BatchService batchService;
    private final UserStore userStore;
    private final JwtService jwtService;

    public AutoServiceImpl(CatalogService catalogService,
                        AnalysisService analysisService,
                        BatchService batchService,
                        UserStore userStore,
                        JwtService jwtService) {
        this.catalogService = catalogService;
        this.analysisService = analysisService;
        this.batchService = batchService;
        this.userStore = userStore;
        this.jwtService = jwtService;
    }

    /** Permette di verificare il ruolo dell'utente corrente */
    private void requireRole(Role required) {
        if (CurrentUserContext.getRole() != required) {
            throw new SecurityException("Operazione riservata al ruolo " + required);
        }
    }

    // ===================== CATALOGO =====================

    @Override
    public List<BrandDTO> getBrands() {
        return catalogService.getBrands();
    }

    // Solo SELLER
    @Override
    public CatalogPageDTO getCatalog(CatalogQueryDTO query) {
        requireRole(Role.SELLER);
        return catalogService.getCatalog(query);
    }

    // Solo SELLER
    @Override
    public CarDetailDTO getCarDetail(long carId) {
        requireRole(Role.SELLER);
        return catalogService.getCarDetail(carId);
    }

    // ===================== ANALISI =====================

    // Solo ANALYST
    @Override
    public AnalysisResultDTO runAnalysis(AnalysisRequestDTO request) {
        requireRole(Role.ANALYST);
        return analysisService.run(request);
    }

    // ===================== BATCH =====================

    @Override
    public BatchResponseDTO executeBatch(BatchRequestDTO batch) {
        return batchService.execute(batch);
    }

    // ===================== AUTENTICAZIONE =====================

    /**
     * Authenticator: verifica le credenziali e, se valide, emette il token
     * (la "proof" del pattern).
     */
    @Override
    public AuthTokenDTO login(CredentialsDTO credentials) {
        Optional<UserStore.User> user =
                userStore.authenticate(credentials.getUsername(), credentials.getPassword());

        UserStore.User u = user.orElseThrow(
                () -> new RuntimeException("Credenziali non valide"));

        String token = jwtService.generateToken(u.username(), u.role());
        return new AuthTokenDTO(token, u.username(), u.role());
    }
}