package com.progetto.shared.dto.batch;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Il batch di risposte restituito da AutoService.executeBatch().
 * Ogni ResponseItemDTO è appaiata alla richiesta originale
 * tramite il requestId condiviso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponseDTO {
    private List<ResponseItemDTO> responses;   // una risposta per ogni richiesta del batch
}