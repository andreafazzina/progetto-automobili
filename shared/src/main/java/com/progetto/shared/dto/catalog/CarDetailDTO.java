package com.progetto.shared.dto.catalog;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO che rappresenta i dettagli di un'auto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDetailDTO {
    private long id;                  // identificativo dell'auto

    private String make;
    private String model;
    private int year;
    private String fuelType;
    private String transmission;
    private double engineSize;        // cilindrata (float nel CSV)
    private int mileage;
    private double horsepower;        // potenza (float nel CSV)
    private double torque;            // coppia (float nel CSV)
    private int owners;               // numero di proprietari precedenti
    private double accidentHistory;   // storico incidenti (float nel CSV)
    private String serviceHistory;    // storico tagliandi
    private String color;
    private String bodyType;
    private String drivetrain;
    private double fuelEfficiency;    // efficienza (float nel CSV)
    private String location;
    private int sellingPrice;
}