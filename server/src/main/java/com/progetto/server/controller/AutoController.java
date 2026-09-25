package com.progetto.server.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.progetto.shared.dto.catalog.*;
import com.progetto.shared.service.AutoService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.progetto.shared.dto.analysis.AnalysisRequestDTO;
import com.progetto.shared.dto.analysis.AnalysisResultDTO;
import com.progetto.shared.dto.auth.AuthTokenDTO;
import com.progetto.shared.dto.auth.CredentialsDTO;
import com.progetto.shared.dto.batch.BatchRequestDTO;
import com.progetto.shared.dto.batch.BatchResponseDTO;

/**
 * Punto d'ingresso HTTP del server (Remote Facade su REST).
 * Traduce le richieste HTTP in chiamate al service e restituisce
 * i DTO, che Spring serializza automaticamente in JSON.
 */
@RestController
@RequestMapping("/api")
public class AutoController {

    private final AutoService autoService;
    public AutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    // home: elenco brand con contatore
    @GetMapping("/brands")
    public List<BrandDTO> getBrands() {
        return autoService.getBrands();
    }

    // auto del brand selezionato
    @GetMapping("/catalog/{make}")
    public CatalogPageDTO getCatalog(
            @PathVariable String make,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer priceFrom,
            @RequestParam(required = false) Integer priceTo,
            @RequestParam(required = false) Integer mileageMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "0") int page) {

        CatalogQueryDTO query = new CatalogQueryDTO(
                make, bodyType, fuelType,
                yearFrom, yearTo, priceFrom, priceTo, mileageMax,
                sortBy, sortDir, page
        );
        return autoService.getCatalog(query);
    }

    // dettaglio di una singola auto
    @GetMapping("/cars/{id}")
    public CarDetailDTO getCarDetail(@PathVariable long id) {
        return autoService.getCarDetail(id);
    }

    // esegue un'analisi aggregata (solo ANALYST)
    @PostMapping("/analysis")
    public AnalysisResultDTO runAnalysis(@RequestBody AnalysisRequestDTO request) {
        return autoService.runAnalysis(request);
    }

    // esegue un lotto di richieste indipendenti
    @PostMapping("/batch")
    public BatchResponseDTO executeBatch(@RequestBody BatchRequestDTO batch) {
        return autoService.executeBatch(batch);
    }

    // autenticazione, restituisce il token
    @PostMapping("/login")
    public AuthTokenDTO login(@RequestBody CredentialsDTO credentials) {
        return autoService.login(credentials);
    }
}