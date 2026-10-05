package com.progetto.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.progetto.shared.dto.analysis.*;
import com.progetto.shared.dto.batch.*;
import com.progetto.shared.service.AutoService;
import com.progetto.client.session.SessionState;

/**
 * Controller MVC delle analisi (riservate all'ANALYST).
 * Mostra il form di scelta dell'analisi e, quando richiesto,
 * esegue l'analisi tramite il proxy e ne mostra i risultati.
 */
@Controller
public class AnalysisController {

    private final AutoService autoService;
    private final SessionState session;

    public AnalysisController(AutoService autoService, SessionState session) {
        this.autoService = autoService;
        this.session = session;
    }

    /** form di scelta, ed eventualmente il risultato. */
    @GetMapping("/analysis")
    public String analysis(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String make,
            Model model) {

        // Verifica sull'utente
        if (!session.isAuthenticated()) return "redirect:/login";
        if (!session.isAnalyst()) return "redirect:/";

        model.addAttribute("username", session.getUsername());

        // Dati per i menu a tendina
        model.addAttribute("types", AnalysisType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("brands", autoService.getBrands());
        model.addAttribute("selectedMake", make);

        // Filtro marca opzionale, condiviso da tutte le analisi
        Map<String, String> params = (make != null && !make.isBlank())
                ? Map.of("make", make)
                : Map.of();

        // Esecuzione
        if ("ALL".equals(type)) {
            // Tutte le analisi in un solo batch
            model.addAttribute("results", runAllAnalyses(params));
        } else if (type != null && !type.isBlank()) {
            // Singola analisi
            AnalysisType at = AnalysisType.valueOf(type);
            AnalysisResultDTO single = autoService.runAnalysis(new AnalysisRequestDTO(at, params));
            model.addAttribute("results", List.of(single));
        }
        // se type è null (primo accesso alla pagina), nessun risultato: solo il form

        return "analysis";
    }

    /**
     * Costruisce un batch con una richiesta per ogni AnalysisType, lo esegue,
     * e ricostruisce la lista di AnalysisResultDTO dai risultati.
     * È qui che il Request Batch viene esercitato per le analisi:
     * BatchService smista ogni richiesta ad AnalysisService.
     */
    private List<AnalysisResultDTO> runAllAnalyses(Map<String, String> params) {
        // 1. Una richiesta di batch per ogni tipo di analisi
        List<RequestItemDTO> requests = new ArrayList<>();
        
        for (AnalysisType at : AnalysisType.values()) {
            RequestType rt = RequestType.valueOf(at.name());
            requests.add(new RequestItemDTO(at.name(), rt, params));
        }

        // 2. Una sola chiamata batch
        BatchResponseDTO response = autoService.executeBatch(new BatchRequestDTO(requests));

        // 3. Ricostruisce un AnalysisResultDTO da ogni risposta OK
        List<AnalysisResultDTO> results = new ArrayList<>();
        if (response != null && response.getResponses() != null) {
            for (var item : response.getResponses()) {
                if (item.getStatus() == ResponseStatus.OK && item.getPayload() != null) {
                    PayloadDTO p = item.getPayload();
                    AnalysisResultDTO r = new AnalysisResultDTO();
                    r.setType(AnalysisType.valueOf(item.getRequestId()));
                    r.setTitle(p.getTitle());
                    r.setKey(p.getKey());
                    r.setValue(p.getValue());
                    r.setData(p.getDataPoints());
                    results.add(r);
                }
            }
        }
        return results;
    }
}