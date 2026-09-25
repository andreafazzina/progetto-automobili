package com.progetto.server.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.progetto.server.entity.Automobile;
import com.progetto.shared.dto.catalog.CatalogQueryDTO;

import jakarta.persistence.criteria.Predicate;

/**
 * Costruisce dinamicamente la query di catalogo: aggiunge una
 * condizione WHERE solo per ogni filtro effettivamente valorizzato
 * nel CatalogQueryDTO. I filtri null vengono ignorati.
 * È il meccanismo idiomatico di Spring Data per query con filtri
 * opzionali e combinabili, senza esplosione di metodi.
 */
public class CatalogSpecification {

    /**
     * Produce una Specification a partire dai criteri della query.
     * Ogni "if" aggiunge una condizione solo se il relativo filtro è presente.
     */
    public static Specification<Automobile> fromQuery(CatalogQueryDTO q) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro obbligatorio: la marca
            predicates.add(cb.equal(root.get("make"), q.getMake()));

            // Filtri opzionali: aggiunti solo se valorizzati
            if (q.getBodyType() != null && !q.getBodyType().isBlank()) {
                predicates.add(cb.equal(root.get("bodyType"), q.getBodyType()));
            }
            if (q.getFuelType() != null && !q.getFuelType().isBlank()) {
                predicates.add(cb.equal(root.get("fuelType"), q.getFuelType()));
            }
            if (q.getYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), q.getYearFrom()));
            }
            if (q.getYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), q.getYearTo()));
            }
            if (q.getPriceFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), q.getPriceFrom()));
            }
            if (q.getPriceTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), q.getPriceTo()));
            }
            if (q.getMileageMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("mileage"), q.getMileageMax()));
            }

            // Combina tutte le condizioni con AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}