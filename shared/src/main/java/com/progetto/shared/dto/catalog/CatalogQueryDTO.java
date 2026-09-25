package com.progetto.shared.dto.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO che rappresenta i parametri di ricerca e filtraggio per il catalogo auto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogQueryDTO {

    private String make;          // marca selezionata (obbligatoria)

    // --- Filtri opzionali (null = non applicato) ---
    private String bodyType;      // carrozzeria (es. "SUV")
    private String fuelType;      // carburante (es. "Diesel")
    private Integer yearFrom;     // anno minimo
    private Integer yearTo;       // anno massimo
    private Integer priceFrom;    // prezzo minimo
    private Integer priceTo;      // prezzo massimo
    private Integer mileageMax;   // chilometraggio massimo

    // --- Ordinamento ---
    private String sortBy;        // "price", "year", "mileage" (o null = default)
    private String sortDir;       // "asc" o "desc" (default "asc")

    // --- Paginazione ---
    private int page;             // 0-based
}