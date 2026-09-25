package com.progetto.shared.dto.analysis;

/**
 * Tipi di analisi aggregata disponibili (operazioni da ANALYST).
 * Ogni valore corrisponde a un algoritmo di analisi lato server
 * che produce una List<DataPointDTO>.
 */
public enum AnalysisType {

    AVG_PRICE("Prezzo medio"),
    DEPRECIATION_CURVE("Curva di deprezzamento"),
    MILEAGE_PRICE_IMPACT("Impatto chilometraggio"),
    ACCIDENT_PRICE_IMPACT("Impatto incidenti"),
    PRICE_BY_LOCATION("Prezzo per località");

    private final String displayName;

    AnalysisType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}