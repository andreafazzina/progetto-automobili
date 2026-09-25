package com.progetto.shared.dto.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO che rappresenta un brand e il numero di auto disponibili di quel brand.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
    private String make;          // nome del brand (es. "Audi")
    private int availableCars;    // quante auto di quel brand sono disponibili
}