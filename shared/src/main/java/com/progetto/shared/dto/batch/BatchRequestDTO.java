package com.progetto.shared.dto.batch;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Il batch inviato dal client a AutoService.executeBatch().
 * Aggrega più richieste indipendenti in un unico round-trip.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequestDTO {
    private List<RequestItemDTO> requests;   // le richieste accumulate dal client
}