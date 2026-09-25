package com.progetto.shared.dto.batch;

/**
 * Tipi di richiesta che possono comparire in un batch.
 * Ogni RequestItemDTO ne porta uno. Il server, disaggregando
 * il batch, instrada ciascuna richiesta in base a questo tipo
 * e ne verifica l'accessibilità rispetto al ruolo nel token.
 */
public enum RequestType {

    // --- Richieste di similarità (accessibili a USER e ANALYST) ---
    // Producono List<CarSummaryDTO> → payload.cars
    SIMILAR_BY_PRICE,      // auto simili per fascia di prezzo, altra marca
    SIMILAR_BY_ENGINE,     // auto simili per motorizzazione (cilindrata/potenza)
    SIMILAR_BY_USAGE,      // auto simili per anno e chilometraggio
    SIMILAR_BY_CATEGORY,   // stessa categoria (body type/drivetrain), prezzo inferiore

    // --- Analisi aggregate (solo ANALYST) ---
    // Producono List<DataPointDTO> → payload.dataPoints
    AVG_PRICE,
    DEPRECIATION_CURVE,
    MILEAGE_PRICE_IMPACT,
    ACCIDENT_PRICE_IMPACT,
    PRICE_BY_LOCATION
}