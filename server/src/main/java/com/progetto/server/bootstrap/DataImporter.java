package com.progetto.server.bootstrap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.progetto.server.entity.Automobile;
import com.progetto.server.repository.AutomobileRepository;

/**
 * Popola il database all'avvio leggendo il CSV, ma solo se la
 * tabella è vuota (import "una volta sola"). Ai riavvii successivi
 * trova i dati già presenti e non fa nulla.
 */
@Component
public class DataImporter implements CommandLineRunner {

    private static final String CSV_FILE = "automobile_dataset.csv";

    private final AutomobileRepository repository;
    public DataImporter(AutomobileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (repository.count() > 0) {
            System.out.println("[DataImporter] Tabella già popolata (" 
                + repository.count() + " auto). Import saltato.");
            return;
        }

        System.out.println("[DataImporter] Tabella vuota. Avvio import da " + CSV_FILE);

        List<Automobile> autos = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource(CSV_FILE);
        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // Salta la riga di intestazione
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Salta eventuali righe vuote
                if (line.isBlank()) {
                    continue;
                }

                Automobile auto = parseLine(line);
                autos.add(auto);
            }
        }

        repository.saveAll(autos);

        System.out.println("[DataImporter] Import completato: " 
            + autos.size() + " auto inserite.");
    }

    /**
     * Converte una riga del CSV in un oggetto Automobile.
     * L'ordine dei campi rispecchia esattamente le colonne del dataset.
     */
    private Automobile parseLine(String line) {
        // -1 mantiene eventuali campi finali vuoti (qui non ce ne sono,
        // ma è più robusto). Il CSV non ha virgole interne, split sicuro.
        String[] f = line.split(",", -1);

        Automobile auto = new Automobile();
        auto.setMake(f[0].trim());
        auto.setModel(f[1].trim());
        auto.setYear(Integer.parseInt(f[2].trim()));
        auto.setFuelType(f[3].trim());
        auto.setTransmission(f[4].trim());
        auto.setEngineSize(Double.parseDouble(f[5].trim()));
        auto.setMileage(Integer.parseInt(f[6].trim()));
        auto.setHorsepower(Double.parseDouble(f[7].trim()));
        auto.setTorque(Double.parseDouble(f[8].trim()));
        auto.setOwners(Integer.parseInt(f[9].trim()));
        auto.setAccidentHistory(Double.parseDouble(f[10].trim()));
        auto.setServiceHistory(f[11].trim());
        auto.setColor(f[12].trim());
        auto.setBodyType(f[13].trim());
        auto.setDrivetrain(f[14].trim());
        auto.setFuelEfficiency(Double.parseDouble(f[15].trim()));
        auto.setLocation(f[16].trim());
        auto.setSellingPrice(Integer.parseInt(f[17].trim()));

        return auto;
    }
}