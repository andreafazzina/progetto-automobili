package com.progetto.shared.dto.batch;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Singola richiesta all'interno di un batch.
 * Il client ne accumula diverse in un BatchRequestDTO.
 * Il requestId, generato dal client, permette di rimappare
 * la risposta corrispondente al ritorno del batch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDTO {
    private String requestId;            // ID generato dal client, per rimappare la risposta
    private RequestType type;            // cosa chiede questa richiesta
    private Map<String, String> params;  // parametri, dipendenti dal tipo
}