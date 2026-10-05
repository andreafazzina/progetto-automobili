package com.progetto.client.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.progetto.client.session.SessionState;
import com.progetto.shared.dto.catalog.CatalogQueryDTO;
import com.progetto.shared.service.AutoService;

/**
 * Controller MVC del catalogo: mostra le auto di un brand.
 * Riceve il brand dall'URL, chiede al proxy le auto di quel brand,
 * e passa il CatalogPageDTO al template.
 */
@Controller
public class CatalogController {

    private final AutoService autoService;
    private final SessionState session;

    public CatalogController(AutoService autoService, SessionState session) {
        this.autoService = autoService;
        this.session = session;
    }

    @GetMapping("/catalog/{make}")
    public String catalog(
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
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Verifica sull'utente
        if (!session.isAuthenticated()) return "redirect:/login";
        if (session.isAnalyst()) return "redirect:/analysis";

        CatalogQueryDTO query = new CatalogQueryDTO(
                make, bodyType, fuelType,
                yearFrom, yearTo, priceFrom, priceTo, mileageMax,
                sortBy, sortDir, page);

        model.addAttribute("username", session.getUsername());
        model.addAttribute("catalog", autoService.getCatalog(query));
        model.addAttribute("query", query);   // per ripopolare il form e i link

        // Valori per i menu a tendina (carrozzerie e carburanti esistenti)
        model.addAttribute("bodyTypes", List.of("SUV", "Sedan", "Hatchback", "Coupe", "Truck"));
        model.addAttribute("fuelTypes", List.of("Petrol", "Diesel", "Electric", "Hybrid"));

        return "catalog";
    }
}