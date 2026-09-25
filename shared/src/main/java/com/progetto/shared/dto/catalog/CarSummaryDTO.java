package com.progetto.shared.dto.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO che rappresenta una vista sintetica di un'auto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSummaryDTO {

    private long id;              // chiave primaria dell'auto (identificativo univoco)

    private String make;
    private String model;
    private int year;
    private String fuelType;
    private String transmission;
    private int mileage;
    private String bodyType;
    private int sellingPrice;
}