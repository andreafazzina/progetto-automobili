package com.progetto.shared.dto.analysis;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Output dell'operazione AutoService.runAnalysis().
 * Avvolge la lista di DataPointDTO con le etichette che ne
 * spiegano il significato.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultDTO {
    private AnalysisType type;          // eco del tipo di analisi eseguita
    private String title;               // titolo leggibile: Tipologia di analisi
    private String key;                 // significato delle chiavi: "Marca"
    private String value;               // significato dei valori: "Prezzo medio (€)"
    private List<DataPointDTO> data;    // i punti dati veri e propri
}