package com.progetto.shared.dto.batch;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Risposta a una singola richiesta del batch.
 * Porta lo stesso requestId della RequestItemDTO corrispondente,
 * così il client può smistarla al pannello giusto.
 * Coerenza dei campi:
 *  - status OK       -> payload valorizzato, errorMessage null
 *  - status != OK    -> payload null, errorMessage spiega il motivo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseItemDTO {
    private String requestId;        // stesso ID della richiesta: il ponte per lo smistamento
    private ResponseStatus status;   // esito: OK / FORBIDDEN / ERROR
    private PayloadDTO payload;       // risultato (solo se status == OK)
    private String errorMessage;     // motivo del fallimento (solo se status != OK)
}