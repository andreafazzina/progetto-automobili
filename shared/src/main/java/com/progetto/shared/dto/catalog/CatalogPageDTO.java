package com.progetto.shared.dto.catalog;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO che rappresenta una pagina del catalogo di un brand.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPageDTO {
    private String make;                 // il brand selezionato
    private List<CarSummaryDTO> cars;    // SOLO le auto di questa pagina (es. 20)

    private int page;                    // pagina corrente (0-based)
    private int totalPages;              // numero totale di pagine
    private long totalCars;              // totale auto del brand (tutte le pagine)
    private boolean hasPrevious;         // esiste una pagina precedente?
    private boolean hasNext;             // esiste una pagina successiva?
}