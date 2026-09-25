package com.progetto.server.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.progetto.server.assembler.AutoAssembler;
import com.progetto.server.entity.Automobile;
import com.progetto.server.repository.*;
import com.progetto.shared.dto.catalog.*;

/**
 * Servizio specializzato per la gestione del catalogo.
 * Contiene i metodi: getBrands(), getCatalog(), getCarDetail().
 */
@Service
public class CatalogService {

    private static final int PAGE_SIZE = 20;

    private final AutomobileRepository repository;
    private final AutoAssembler assembler;

    public CatalogService(AutomobileRepository repository, AutoAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /** Restituisce una lista di tutti i brand disponibili nel catalogo, con il numero di auto disponibili per ciascun brand */

    public List<BrandDTO> getBrands() {
        Map<String, Long> countByMake = repository.findAll().stream()
                .collect(Collectors.groupingBy(Automobile::getMake, Collectors.counting()));

        return countByMake.entrySet().stream()
                .map(e -> new BrandDTO(e.getKey(), e.getValue().intValue()))
                .sorted((a, b) -> a.getMake().compareTo(b.getMake()))
                .toList();
    }

    /** Restituisce una pagina del catalogo basata sui criteri di ricerca e filtraggio. */
    public CatalogPageDTO getCatalog(CatalogQueryDTO query) {

        // ---- PARAMETRI PER LA PAGINAZIONE, ORDINAMENTO E FILTRI DINAMICI ---- 
        Specification<Automobile> spec = CatalogSpecification.fromQuery(query);         // Costruisce la query dinamica con i filtri valorizzati
        Sort sort = buildSort(query.getSortBy(), query.getSortDir());                   // Costruisce l'ordinamento dinamico con i criteri valorizzati        
        Pageable pageable = PageRequest.of(query.getPage(), PAGE_SIZE, sort);           // Costruisce la paginazione con la pagina richiesta e la dimensione fissa
        Page<Automobile> pageResult = repository.findAll(spec, pageable);               // Costruisce la query finale con filtri, ordinamento e paginazione, e la esegue sul database

        List<CarSummaryDTO> cars = assembler.toSummaryList(pageResult.getContent());    // Converte le entità Automobile in DTO di riepilogo per la risposta

        return new CatalogPageDTO(
                query.getMake(),
                cars,
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.hasPrevious(),
                pageResult.hasNext()
        );
    }

    /** Restituisce i dettagli completi di un'auto specifica, identificata dal suo ID. */
    public CarDetailDTO getCarDetail(long carId) {
        Automobile auto = repository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Auto non trovata: id " + carId));
        return assembler.toDetail(auto);
    }

    // -------- Metodi privati di supporto --------

    /** Traduce i criteri di ordinamento del client nel Sort di Spring. */
    private Sort buildSort(String sortBy, String sortDir) {
        String field = switch (sortBy == null ? "" : sortBy) {
            case "price"   -> "sellingPrice";
            case "year"    -> "year";
            case "mileage" -> "mileage";
            default        -> "id";
        };
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}