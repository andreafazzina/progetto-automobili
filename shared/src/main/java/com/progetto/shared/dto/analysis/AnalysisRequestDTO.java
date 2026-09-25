package com.progetto.shared.dto.analysis;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Input dell'operazione AutoService.runAnalysis().
 * Specifica quale analisi eseguire e i suoi eventuali parametri.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDTO {
    private AnalysisType type;           // quale analisi eseguire
    private Map<String, String> params;  // parametri opzionali, dipendenti dal tipo
}