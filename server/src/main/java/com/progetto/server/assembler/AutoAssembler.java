package com.progetto.server.assembler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.progetto.server.entity.Automobile;
import com.progetto.shared.dto.catalog.*;

/**
 * Traduce l'entità Automobile nei DTO usati per la visualizzazione del catalogo.
 * Nessun altro componente del server esegue tale lavoro.
 * Lo scopo è nascondere l'entità al client.
 */
@Component
public class AutoAssembler {

    /**
     * Entity -> vista sintetica (per la lista del catalogo).
     */
    public CarSummaryDTO toSummary(Automobile a) {
        CarSummaryDTO dto = new CarSummaryDTO();
        dto.setId(a.getId());
        dto.setMake(a.getMake());
        dto.setModel(a.getModel());
        dto.setYear(a.getYear());
        dto.setFuelType(a.getFuelType());
        dto.setTransmission(a.getTransmission());
        dto.setMileage(a.getMileage());
        dto.setBodyType(a.getBodyType());
        dto.setSellingPrice(a.getSellingPrice());
        return dto;
    }

    /**
     * Entity -> scheda completa (per il dettaglio).
     */
    public CarDetailDTO toDetail(Automobile a) {
        CarDetailDTO dto = new CarDetailDTO();
        dto.setId(a.getId());
        dto.setMake(a.getMake());
        dto.setModel(a.getModel());
        dto.setYear(a.getYear());
        dto.setFuelType(a.getFuelType());
        dto.setTransmission(a.getTransmission());
        dto.setEngineSize(a.getEngineSize());
        dto.setMileage(a.getMileage());
        dto.setHorsepower(a.getHorsepower());
        dto.setTorque(a.getTorque());
        dto.setOwners(a.getOwners());
        dto.setAccidentHistory(a.getAccidentHistory());
        dto.setServiceHistory(a.getServiceHistory());
        dto.setColor(a.getColor());
        dto.setBodyType(a.getBodyType());
        dto.setDrivetrain(a.getDrivetrain());
        dto.setFuelEfficiency(a.getFuelEfficiency());
        dto.setLocation(a.getLocation());
        dto.setSellingPrice(a.getSellingPrice());
        return dto;
    }

    /**
     * Converte una lista di Automobili in una lista di DTO
     */
    public List<CarSummaryDTO> toSummaryList(List<Automobile> autos) {
        return autos.stream()
                    .map(this::toSummary)
                    .toList();
    }
}