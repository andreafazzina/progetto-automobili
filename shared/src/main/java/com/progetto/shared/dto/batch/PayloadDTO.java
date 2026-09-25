package com.progetto.shared.dto.batch;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.progetto.shared.dto.catalog.CarSummaryDTO;
import com.progetto.shared.dto.analysis.DataPointDTO;

/**
 * Wrapper del risultato di una singola richiesta del batch.
 * Solo uno dei due campi è valorizzato, in base al RequestType:
 *  - richieste SIMILAR_*    -> cars
 *  - richieste di analisi   -> dataPoints
 * L'altro campo resta null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayloadDTO {
    private List<CarSummaryDTO> cars;        // valorizzato per similarità
    private List<DataPointDTO> dataPoints;   // valorizzato per le analisi

    // Necessarie per le analisi, inutilizzate per le similarità
    private String title;
    private String key;
    private String value;
}