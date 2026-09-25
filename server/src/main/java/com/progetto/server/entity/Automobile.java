package com.progetto.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * Rappresenta il veicolo nudo e crudo.
 * Fa uso delle annotazioni JPA.
 * Questa entità non lascia mai il server.
 */
@Entity
@Table(name = "automobiles")

/** funzionalità ricavate dalla libreria lombok ci permette di semplificare la scrittura del codice */
@Getter                 // genera automaticamente i metodi getter per tutti gli attributi
@Setter                 // genera automaticamente i metodi setter per tutti gli attributi
@NoArgsConstructor      // genera un costruttore senza argomenti (necessario a JPA)
@AllArgsConstructor     // genera un costruttore con tutti gli argomenti (utile per test e debug)
public class Automobile {

    /** 
     * @Id - Chiave primaria
     * @GeneratedValue() - Viene assegnato un numero univoco incrementale a ogni nuova auto inserita.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 

    /** 18 Attributi 
     * @Columm(nullable = false) - l'attributo non può essere nullo nel database
     */
    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    private int year;

    @Column(nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private String transmission;

    private double engineSize;

    private int mileage;

    private double horsepower;

    private double torque;

    private int owners;

    private double accidentHistory;   // 0.0 = nessun incidente, 1.0 = incidente

    @Column(nullable = false)
    private String serviceHistory;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String bodyType;

    @Column(nullable = false)
    private String drivetrain;

    private double fuelEfficiency;

    @Column(nullable = false)
    private String location;

    private int sellingPrice;
}