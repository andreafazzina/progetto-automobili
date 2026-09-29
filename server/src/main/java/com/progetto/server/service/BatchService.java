package com.progetto.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.progetto.shared.dto.auth.Role;
import com.progetto.shared.dto.batch.*;
import com.progetto.shared.dto.analysis.AnalysisType;
import com.progetto.server.security.CurrentUserContext;
import com.progetto.shared.dto.analysis.AnalysisRequestDTO;
import com.progetto.shared.dto.analysis.AnalysisResultDTO;

/**
 * Orchestrazione del Request Batch.
 * Non contiene logica di dominio: disaggrega il lotto, autorizza
 * ogni richiesta per ruolo, la instrada verso il service competente
 * (similarità/analisi), isola i fallimenti e ricompone le risposte
 * appaiate per requestId.
 */
@Service
public class BatchService {

    private final AnalysisService analysisService;
    private final SimilarityService similarityService;

    public BatchService(AnalysisService analysisService,
                        SimilarityService similarityService) {
        this.analysisService = analysisService;
        this.similarityService = similarityService;
    }

    /**
     * Punto d'ingresso: elabora l'intero batch.
     * Ogni richiesta è indipendente; un fallimento non tocca le altre.
     */
    public BatchResponseDTO execute(BatchRequestDTO batch) {
        Role role = getCurrentRole();   

        List<ResponseItemDTO> responses = new ArrayList<>();

        if (batch.getRequests() != null) {
            for (RequestItemDTO item : batch.getRequests()) {
                responses.add(dispatch(item, role));
            }
        }

        return new BatchResponseDTO(responses);
    }

    /**
     * Elabora una singola richiesta, isolando i fallimenti:
     * un errore su una richiesta NON compromette le altre.
     */
    private ResponseItemDTO dispatch(RequestItemDTO item, Role role) {
        ResponseItemDTO resp = new ResponseItemDTO();
        resp.setRequestId(item.getRequestId());   // il ponte con la richiesta originale

        try {
            if (!isAllowed(item.getType(), role)) {
                resp.setStatus(ResponseStatus.FORBIDDEN);
                resp.setErrorMessage("Operazione non consentita per il ruolo " + role);
                return resp;
            }
            PayloadDTO payload = runRequest(item);      // esecuzione effettiva della richiesta
            resp.setStatus(ResponseStatus.OK);
            resp.setPayload(payload);

        } catch (Exception ex) {
            resp.setStatus(ResponseStatus.ERROR);
            resp.setErrorMessage(ex.getMessage());
        }

        return resp;
    }

    /**
     * Instrada la richiesta verso la logica corretta e impacchetta
     * il risultato nel PayloadDTO (cars oppure dataPoints).
     */
    private PayloadDTO runRequest(RequestItemDTO item) {
        RequestType type = item.getType();
        PayloadDTO payload = new PayloadDTO();

        switch (type) {
            case SIMILAR_BY_PRICE, SIMILAR_BY_ENGINE, SIMILAR_BY_USAGE, SIMILAR_BY_CATEGORY ->
                payload.setCars(similarityService.findSimilar(type, item.getParams()));

            case AVG_PRICE, DEPRECIATION_CURVE, MILEAGE_PRICE_IMPACT,
                 ACCIDENT_PRICE_IMPACT, PRICE_BY_LOCATION -> {
                AnalysisType at = AnalysisType.valueOf(type.name());
                AnalysisResultDTO result = analysisService.run(
                        new AnalysisRequestDTO(at, item.getParams()));
                payload.setDataPoints(result.getData());
                payload.setTitle(result.getTitle());
                payload.setKey(result.getKey());
                payload.setValue(result.getValue());
            }

            default ->
                throw new IllegalArgumentException("Tipo di richiesta sconosciuto: " + type);
        }

        return payload;
    }

    // Mutua esclusione
    private boolean isAllowed(RequestType type, Role role) {
        if (role == null) return false;   // non autenticato: niente

        boolean isSimilarity = switch (type) {
            case SIMILAR_BY_PRICE, SIMILAR_BY_ENGINE,
                SIMILAR_BY_USAGE, SIMILAR_BY_CATEGORY -> true;
            default -> false;   // le altre sono analisi
        };

        return switch (role) {
            case SELLER  -> isSimilarity;    // venditore: solo similarità
            case ANALYST -> !isSimilarity;   // analista: solo analisi
        };
    }

    /**
     * Ruolo dell'utente della richiesta corrente, letto dal contesto
     * popolato dal filtro JWT (il Subject del Reference Monitor).
     * Se la richiesta non è autenticata, restituisce null → nessun
     * tipo di richiesta sarà consentito.
     */
    private Role getCurrentRole() {
        return CurrentUserContext.getRole();
    }
}