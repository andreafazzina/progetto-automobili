package com.progetto.shared.service;

import java.util.List;

import com.progetto.shared.dto.auth.*;
import com.progetto.shared.dto.catalog.*;
import com.progetto.shared.dto.analysis.*;
import com.progetto.shared.dto.batch.*;

/**
 * interfaccia Remote Facade
 * 
 * Definisce le operazioni coarse-grained esposte dal server
 * e usate dal client. Vive nel modulo 'shared' così che
 * entrambe le parti dipendano dallo stesso contratto.
 */
public interface AutoService {

// ===== AUTENTICAZIONE (pubblica) =====

    /** Ritorna un token di autenticazione validato */
    AuthTokenDTO login(CredentialsDTO credentials);

// ===== CATALOGO (USER e ANALYST) =====

    /** Restituisce una lista di tutti i brand disponibili nel catalogo, con il numero di auto disponibili per ciascun brand */
    List<BrandDTO> getBrands();

    /** Restituisce una pagina del catalogo basata sui criteri di ricerca e filtraggio. */
    CatalogPageDTO getCatalog(CatalogQueryDTO query);

    /** Restituisce i dettagli completi di un'auto specifica, identificata dal suo ID. */
    CarDetailDTO getCarDetail(long carId);

// ===== ANALISI (solo ANALYST) =====

    /** Esegue un'analisi sui dati del catalogo */
    AnalysisResultDTO runAnalysis(AnalysisRequestDTO request);

// ===== REQUEST BATCH (USER e ANALYST, con validazione per singola richiesta) =====

    /** Esegue un batch di richieste */
    BatchResponseDTO executeBatch(BatchRequestDTO batch);
}