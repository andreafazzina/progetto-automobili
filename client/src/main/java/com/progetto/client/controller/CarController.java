package com.progetto.client.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.progetto.client.session.SessionState;
import com.progetto.shared.dto.batch.*;
import com.progetto.shared.dto.catalog.CarSummaryDTO;
import com.progetto.shared.service.AutoService;

@Controller
public class CarController {

    private final AutoService autoService;
    private final SessionState session;

    public CarController(AutoService autoService, SessionState session) {
        this.autoService = autoService;
        this.session = session;
    }

    /** Vista per un pannello di auto simili: titolo + lista auto. */
    public record SimilarPanel(String title, List<CarSummaryDTO> cars) {}

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable long id, Model model) {
        
        // Verifica sull'utente
        if (!session.isAuthenticated()) return "redirect:/login";
        if (session.isAnalyst()) return "redirect:/analysis";

        // 1. La scheda dell'auto
        model.addAttribute("username", session.getUsername());
        model.addAttribute("car", autoService.getCarDetail(id));

        // 2. Auto simili: UNA sola chiamata batch con quattro richieste
        BatchResponseDTO response = autoService.executeBatch(buildSimilarBatch(id));

        // 3. Smista ogni risposta al pannello giusto, per requestId
        List<SimilarPanel> panels = List.of(
                new SimilarPanel("Simili per prezzo",  carsFor(response, "price")),
                new SimilarPanel("Simili per motore",  carsFor(response, "engine")),
                new SimilarPanel("Simili per uso",     carsFor(response, "usage")),
                new SimilarPanel("Stessa categoria",   carsFor(response, "category"))
        );
        model.addAttribute("panels", panels);

        return "car-detail";
    }

    /** Costruisce il batch: quattro richieste SIMILAR_*, tutte sullo stesso carId. */
    private BatchRequestDTO buildSimilarBatch(long carId) {
        Map<String, String> params = Map.of("carId", String.valueOf(carId));
        List<RequestItemDTO> requests = List.of(
                new RequestItemDTO("price",    RequestType.SIMILAR_BY_PRICE,    params),
                new RequestItemDTO("engine",   RequestType.SIMILAR_BY_ENGINE,   params),
                new RequestItemDTO("usage",    RequestType.SIMILAR_BY_USAGE,    params),
                new RequestItemDTO("category", RequestType.SIMILAR_BY_CATEGORY, params)
        );
        return new BatchRequestDTO(requests);
    }

    /** Estrae le auto della risposta con quel requestId, o lista vuota se assente/non OK. */
    private List<CarSummaryDTO> carsFor(BatchResponseDTO response, String requestId) {
        if (response == null || response.getResponses() == null) {
            return List.of();
        }
        return response.getResponses().stream()
                .filter(r -> requestId.equals(r.getRequestId()))
                .findFirst()
                .filter(r -> r.getStatus() == ResponseStatus.OK && r.getPayload() != null)
                .map(r -> r.getPayload().getCars())
                .orElse(List.of());
    }
}