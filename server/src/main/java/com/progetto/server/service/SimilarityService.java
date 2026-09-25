package com.progetto.server.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.progetto.server.assembler.AutoAssembler;
import com.progetto.server.entity.Automobile;
import com.progetto.server.repository.AutomobileRepository;
import com.progetto.shared.dto.batch.RequestType;
import com.progetto.shared.dto.catalog.CarSummaryDTO;


/**
 * Logica di similarità tra auto. I quattro tipi SIMILAR_* usano
 * lo STESSO algoritmo a soglie, con pesi diversi a seconda
 * dell'aspetto da privilegiare (prezzo, motore, uso, categoria).
 * Vincolo comune: le auto simili sono sempre di UN'ALTRA marca.
 */
@Service
public class SimilarityService {

    // ---- Numero di risultati restituiti ----
    private static final int K = 5;

    // ---- Soglie di "vicinanza" (ritoccabili) ----
    private static final double PRICE_TOLERANCE = 0.10;   // ±10% del prezzo di riferimento
    private static final int    YEAR_TOLERANCE  = 2;      // ±2 anni
    private static final int    MILEAGE_TOLERANCE = 30000;// ±30.000 km
    private static final double ENGINE_TOLERANCE = 0.3;   // ±0.3 L di cilindrata
    private static final double POWER_TOLERANCE  = 30.0;  // ±30 CV

    private final AutomobileRepository repository;
    private final AutoAssembler assembler;

    public SimilarityService(AutomobileRepository repository, AutoAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * Punto d'ingresso: trova le auto simili all'auto indicata da params["carId"],
     * secondo il criterio (i pesi) associato al tipo di richiesta.
     */
    public List<CarSummaryDTO> findSimilar(RequestType type, Map<String, String> params) {

        // 1. Estrai il carId (Sostituito con IllegalArgumentException standard)
        if (params == null || !params.containsKey("carId")) {
            throw new IllegalArgumentException("Parametro obbligatorio mancante: carId");
        }
        long carId = Long.parseLong(params.get("carId").trim());

        // 2. Carica l'auto di riferimento (Sostituito con RuntimeException standard)
        Automobile ref = repository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Auto di riferimento non trovata: id " + carId));

        // 3. Pesi da usare, in base al tipo
        Weights w = weightsFor(type);

        // 4. Confronta tutte le altre auto, escludendo stessa marca e sé stessa
        List<Automobile> candidates = repository.findAll();

        return candidates.stream()
                .filter(c -> c.getId() != ref.getId())                       // non sé stessa
                .filter(c -> !c.getMake().equalsIgnoreCase(ref.getMake()))   // altra marca
                .map(c -> new Scored(c, score(ref, c, w)))                   // calcola punteggio
                .filter(s -> s.score > 0)                                    // scarta i non-simili
                .sorted((a, b) -> Integer.compare(b.score, a.score))         // migliori in cima
                .limit(K)                                                     // solo le prime K
                .map(s -> assembler.toSummary(s.car))                        // in vista sintetica
                .toList();
    }

    // ===================== MOTORE DI PUNTEGGIO =====================

    /**
     * Punteggio di similarità tra l'auto di riferimento e una candidata.
     * Ogni criterio soddisfatto aggiunge il proprio peso.
     */
    private int score(Automobile ref, Automobile cand, Weights w) {
        int s = 0;

        if (Math.abs(cand.getSellingPrice() - ref.getSellingPrice())
                <= ref.getSellingPrice() * PRICE_TOLERANCE) s += w.price;

        if (Math.abs(cand.getYear() - ref.getYear()) <= YEAR_TOLERANCE) s += w.year;

        if (Math.abs(cand.getMileage() - ref.getMileage()) <= MILEAGE_TOLERANCE) s += w.mileage;

        if (Math.abs(cand.getEngineSize() - ref.getEngineSize()) <= ENGINE_TOLERANCE) s += w.engine;

        if (Math.abs(cand.getHorsepower() - ref.getHorsepower()) <= POWER_TOLERANCE) s += w.power;

        if (cand.getBodyType().equalsIgnoreCase(ref.getBodyType())) s += w.body;

        if (cand.getFuelType().equalsIgnoreCase(ref.getFuelType())) s += w.fuel;

        if (cand.getDrivetrain().equalsIgnoreCase(ref.getDrivetrain())) s += w.drivetrain;

        return s;
    }

    /**
     * Configurazione dei pesi per ciascun tipo di richiesta.
     * È qui che "lo stesso algoritmo" diventa quattro criteri diversi.
     */
    private Weights weightsFor(RequestType type) {
        return switch (type) {
            case SIMILAR_BY_PRICE ->
                new Weights(5, 2, 1, 0, 0, 1, 1, 0);   // prezzo dominante
            case SIMILAR_BY_ENGINE ->
                new Weights(0, 0, 0, 4, 4, 1, 2, 0);   // motore e potenza dominanti
            case SIMILAR_BY_USAGE ->
                new Weights(1, 4, 4, 0, 0, 0, 0, 0);   // anno e km dominanti
            case SIMILAR_BY_CATEGORY ->
                new Weights(1, 0, 0, 0, 0, 4, 1, 4);   // body type e drivetrain dominanti
            default ->
                throw new IllegalArgumentException("Tipo non di similarità: " + type);
        };
    }

    // Struttura interna: i pesi dei criteri. (record = classe dati immutabile)
    private record Weights(
            int price, int year, int mileage,
            int engine, int power,
            int body, int fuel, int drivetrain) {}

    // Struttura interna: un'auto con il suo punteggio, per ordinare.
    private record Scored(Automobile car, int score) {}
}