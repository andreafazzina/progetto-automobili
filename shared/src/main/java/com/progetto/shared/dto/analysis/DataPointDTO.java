package com.progetto.shared.dto.analysis;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Singola coppia etichetta→valore, elemento base di ogni analisi.
 * Una lista di DataPointDTO rappresenta il risultato di
 * un'analisi (es. prezzo medio per marca, deprezzamento per anno).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPointDTO {
    private String key;     // etichetta: "Audi", "2015", "Milano", "0-50000 km"...
    private double value;   // valore numerico associato: 24500.0
}