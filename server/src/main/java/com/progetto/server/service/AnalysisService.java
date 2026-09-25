package com.progetto.server.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.progetto.server.entity.Automobile;
import com.progetto.server.repository.AutomobileRepository;
import com.progetto.shared.dto.analysis.*;

/**
 * Contiene la logica delle analisi aggregate (operazioni da ANALYST).
 * Ogni analisi produce un AnalysisResultDTO: una lista di coppie
 * chiave/valore (DataPointDTO) con le etichette che le descrivono.
 * Isolata da AutoServiceImpl per tenere separata la logica analitica.
 */
@Service
public class AnalysisService {

    private final AutomobileRepository repository;

    public AnalysisService(AutomobileRepository repository) {
        this.repository = repository;
    }

    /**
     * Punto d'ingresso unico: instrada verso l'analisi richiesta.
     */
    public AnalysisResultDTO run(AnalysisRequestDTO request) {
        return switch (request.getType()) {
            case AVG_PRICE -> avgPrice(request.getParams());
            case DEPRECIATION_CURVE -> depreciationCurve(request.getParams());
            case MILEAGE_PRICE_IMPACT -> mileagePriceImpact(request.getParams());
            case ACCIDENT_PRICE_IMPACT -> accidentPriceImpact(request.getParams());
            case PRICE_BY_LOCATION -> priceByLocation(request.getParams());
            default -> throw new UnsupportedOperationException(
                    "Analisi non ancora implementata: " + request.getType());
        };
    }

    // ----- Metodi privati di supporto condivisi da tutte le analisi -----

    /**
     * Restringe la lista alle auto di una marca, SE il parametro "make"
     * è presente e non vuoto; altrimenti restituisce la lista invariata.
     * È il filtro opzionale condiviso da tutte le analisi filtrabili.
     */
    private List<Automobile> filterByMake(List<Automobile> autos, Map<String, String> params) {
        String make = (params != null) ? params.get("make") : null;
        if (make != null && !make.isBlank()) {
            String target = make;
            return autos.stream()
                        .filter(a -> a.getMake().equalsIgnoreCase(target))
                        .toList();
            }
            return autos;
        }


    /** Suffisso per l'etichetta: " – Audi" se filtrato, " (tutte le marche)" altrimenti. */
    private String makeSuffix(Map<String, String> params) {
        String make = (params != null) ? params.get("make") : null;
        return (make != null && !make.isBlank()) ? " - " + make : " (tutte le marche)";
    }

    // ===================== ANALISI 1 =====================

    /**
     * Prezzo medio di vendita.
     * Risultato: (marca, prezzo medio), ordinato dal più caro.
     */
    private AnalysisResultDTO avgPrice(Map<String, String> params) {

        List<Automobile> autos = filterByMake(repository.findAll(), params);

        // Raggruppa per marca e calcola la media del prezzo di vendita
        Map<String, Double> avgByMake = autos.stream()
                .collect(Collectors.groupingBy(
                        Automobile::getMake,
                        Collectors.averagingInt(Automobile::getSellingPrice)));

        // Trasforma in punti dati, arrotonda a euro interi, ordina decrescente
        List<DataPointDTO> data = avgByMake.entrySet().stream()
                .map(e -> new DataPointDTO(e.getKey(), (double) Math.round(e.getValue())))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();

        // Impacchetta con le etichette descrittive
        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setType(AnalysisType.AVG_PRICE);
        result.setTitle("Prezzo medio per marca" + makeSuffix(params));
        result.setKey("Marca");
        result.setValue("Prezzo medio (€)");
        result.setData(data);
        return result;
    }

    // ===================== ANALISI 2 =====================

    /**
     * Curva di deprezzamento: prezzo medio per anno di immatricolazione.
     * Risultato: (anno, prezzo medio), ordinato per anno crescente.
     */
    private AnalysisResultDTO depreciationCurve(Map<String, String> params) {

        List<Automobile> autos = filterByMake(repository.findAll(), params);

        // Raggruppa per anno e calcola il prezzo medio
        Map<Integer, Double> avgByYear = autos.stream()
            .collect(Collectors.groupingBy(
                    Automobile::getYear,
                    Collectors.averagingInt(Automobile::getSellingPrice)));

        // Trasforma in punti dati, ordina per anno crescente
        List<DataPointDTO> data = avgByYear.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> new DataPointDTO(
                    String.valueOf(e.getKey()),
                    (double) Math.round(e.getValue())))
            .toList();

        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setType(AnalysisType.DEPRECIATION_CURVE);
        result.setTitle("Curva di deprezzamento" + makeSuffix(params));
        result.setKey("Anno");
        result.setValue("Prezzo medio (€)");
        result.setData(data);
        return result;
    }

    // ===================== ANALISI 3 =====================

    /**
     * Impatto del chilometraggio sul prezzo: prezzo medio per fascia di km.
     * Le auto vengono raggruppate in intervalli (0–50k, 50k–100k, ...),
     * così si vede come il prezzo cala al crescere dei chilometri.
     * Non usa parametri: opera sull'intero dataset.
     * Risultato: (fascia km, prezzo medio), ordinato per fascia crescente.
     */
    private AnalysisResultDTO mileagePriceImpact(Map<String, String> params) {

        List<Automobile> autos = filterByMake(repository.findAll(), params);

        // Raggruppa per fascia di chilometraggio e calcola il prezzo medio.
        // Uso TreeMap per tenere le fasce già ordinate per soglia inferiore.
        Map<Integer, Double> avgByBand = autos.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getMileage() / 50000,   // indice di fascia: 0,1,2,...
                        java.util.TreeMap::new,
                        Collectors.averagingInt(Automobile::getSellingPrice)));

        // Trasforma ogni indice di fascia in un'etichetta leggibile
        List<DataPointDTO> data = avgByBand.entrySet().stream()
                .map(e -> new DataPointDTO(
                        bandLabel(e.getKey()),
                        (double) Math.round(e.getValue())))
                .toList();

        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setType(AnalysisType.MILEAGE_PRICE_IMPACT);
        result.setTitle("Impatto del chilometraggio sul prezzo" + makeSuffix(params));
        result.setKey("Fascia di chilometraggio (km)");
        result.setValue("Prezzo medio (€)");
        result.setData(data);
        return result;
    }

    /**
     * Converte un indice di fascia (0,1,2,...) nell'etichetta "0–50k", "50k–100k", ...
     */
    private String bandLabel(int bandIndex) {
        int from = bandIndex * 50;        // in migliaia
        int to = from + 50;
        return from + "k-" + to + "k";
    }

    // ===================== ANALISI 4 =====================

    /**
     * Impatto degli incidenti sul prezzo: confronta il prezzo medio
     * tra auto senza storico incidenti (0.0) e auto con incidenti (1.0).
     * Non usa parametri: opera sull'intero dataset.
     * Risultato: due punti dati, "Senza incidenti" e "Con incidenti".
     */
    private AnalysisResultDTO accidentPriceImpact(Map<String, String> params) {

        List<Automobile> autos = filterByMake(repository.findAll(), params);

        // Raggruppa in due sole categorie in base allo storico incidenti
        Map<Boolean, Double> avgByAccident = autos.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAccidentHistory() > 0.0,   // false = nessuno, true = incidente
                        Collectors.averagingInt(Automobile::getSellingPrice)));

        // Costruisco i due punti in ordine fisso: prima "senza", poi "con"
        List<DataPointDTO> data = new java.util.ArrayList<>();
        if (avgByAccident.containsKey(false)) {
            data.add(new DataPointDTO(
                    "Senza incidenti",
                    (double) Math.round(avgByAccident.get(false))));
        }
        if (avgByAccident.containsKey(true)) {
            data.add(new DataPointDTO(
                    "Con incidenti",
                    (double) Math.round(avgByAccident.get(true))));
        }

        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setType(AnalysisType.ACCIDENT_PRICE_IMPACT);
        result.setTitle("Impatto degli incidenti sul prezzo" + makeSuffix(params));
        result.setKey("Storico incidenti");
        result.setValue("Prezzo medio (€)");
        result.setData(data);
        
        return result;
    }

    // ===================== ANALISI 5 =====================

    /**
     * Prezzo medio per località (Location).
     * Non usa parametri: opera sull'intero dataset.
     * Risultato: (località, prezzo medio), ordinato dal più caro.
     */
    private AnalysisResultDTO priceByLocation(Map<String, String> params) {

        List<Automobile> autos = filterByMake(repository.findAll(), params);

        Map<String, Double> avgByLocation = autos.stream()
                .collect(Collectors.groupingBy(
                        Automobile::getLocation,
                        Collectors.averagingInt(Automobile::getSellingPrice)));

        List<DataPointDTO> data = avgByLocation.entrySet().stream()
                .map(e -> new DataPointDTO(e.getKey(), (double) Math.round(e.getValue())))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .toList();

        AnalysisResultDTO result = new AnalysisResultDTO();
        result.setType(AnalysisType.PRICE_BY_LOCATION);
        result.setTitle("Prezzo medio per località" + makeSuffix(params));
        result.setKey("Località");
        result.setValue("Prezzo medio (€)");
        result.setData(data);
        return result;
    }
}