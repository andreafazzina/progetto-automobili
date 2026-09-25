package com.progetto.shared.dto.batch;

/**
 * Esito di una singola richiesta all'interno di un batch.
 * Ogni ResponseItemDTO ne porta uno: così un fallimento
 * isolato non compromette le altre risposte del lotto.
 */
public enum ResponseStatus {
    OK,          // richiesta eseguita con successo, payload valorizzato
    FORBIDDEN,   // ruolo insufficiente per questo tipo di richiesta
    ERROR        // errore di esecuzione (parametri non validi, dati mancanti, ...)
}