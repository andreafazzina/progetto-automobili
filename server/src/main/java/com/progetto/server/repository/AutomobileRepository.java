package com.progetto.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.progetto.server.entity.Automobile;

/**
 * Accesso ai dati delle auto.
 * Estendendo JpaRepository si ottengono gratuitamente le operazioni
 * di base (findAll, findById, save, count, ...).
 */
@Repository
public interface AutomobileRepository extends JpaRepository<Automobile, Long>, JpaSpecificationExecutor<Automobile> {
}

/** JpaSpecificationExecutor sblocca la capacità di eseguire query complesse con criteri dinamici */