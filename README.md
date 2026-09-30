# 🚗 Sistema Distribuito di Consultazione e Analisi Automobilistica

Progetto sviluppato per il corso di **Ingegneria dei Sistemi Distribuiti** (Corso di Laurea Magistrale in Informatica, Università degli Studi di Catania). 

Il sistema implementa un'architettura software distribuita basata su **Spring Boot**, progettata per la consultazione e l'analisi statistica di un dataset automobilistico, garantendo una netta separazione delle responsabilità, scalabilità e una gestione avanzata della sicurezza basata su ruoli (Seller e Analyst).

---

## 🏗️ Architettura del Sistema

L'ecosistema è suddiviso in tre moduli principali gestiti tramite **Maven**:

1. **`shared` (Libreria Comune)**
   * Contiene l'interfaccia di servizio comune (`AutoService`) e tutte le definizioni dei **DTO (Data Transfer Object)** che viaggiano sulla rete. Viene importata come dipendenza sia dal Client che dal Server per garantire coerenza contrattuale.
2. **`client` (Frontend – Porta `8081`)**
   * Applicativo Spring Boot dedicato esclusivamente all'interfaccia utente (**Thymeleaf**) e alla gestione dello stato applicativo in memoria tramite il pattern **Client Session State**.
3. **`server` (Backend – Porta `8080`)**
   * Motore applicativo centrale che espone API REST, gestisce la logica di business, l'autenticazione, la sicurezza e la persistenza dei dati sul database relazionale **PostgreSQL** tramite **Spring Data JPA**.

---

## 📐 Pattern Progettuali Adottati

Il progetto fa ampio uso di pattern architetturali per ottimizzare la comunicazione di rete, disaccoppiare i componenti e garantire la sicurezza:

* **Remote Facade (`AutoServiceImpl`):** Espone interfacce a grana grossa lato server, aggregando le richieste ed evitando un eccessivo traffico di rete. Delega l'elaborazione effettiva a servizi specializzati.
* **Remote Proxy (`AutoServiceProxy`):** Fornisce un surrogato trasparente lato client, intercettando le chiamate locali e incanalandole via HTTP verso il server.
* **Request Batching (`BatchService`):** Permette di accoppiare molteplici operazioni eterogenee in un'unica transazione di rete, riducendo i colli di bottiglia e applicando un controllo di sicurezza granulare (*Zero Trust*).
* **Authenticator & JWT:** Autenticazione stateless basata su token crittografati firmati dal `JwtService` e verificati tramite il `UserStore`.
* **Reference Monitor (`JwtAuthFilter`):** Filtro di sicurezza centralizzato e inaggirabile che intercetta ogni richiesta protetta per validare il token JWT.
* **DTO & Assembler (`AutoAssembler`):** Disaccoppia le entità del database dai dati trasferiti sulla rete, applicando la conversione in modo mirato (es. escluso per le elaborazioni statistiche aggregate).

---

## 🔒 Sicurezza e Gestione della Concorrenza

* **Autenticazione Stateless:** Il server non mantiene registri di sessione o database per i token; ogni richiesta viene validata in memoria verificando la firma crittografica del JWT.
* **Isolamento dei Thread (`CurrentUserContext`):** Sfruttando il costrutto Java `ThreadLocal`, l'identità e il ruolo dell'utente estratti dal filtro di sicurezza vengono isolati sul thread Tomcat corrente, prevenendo race condition ed eliminando la necessità di propagare l'utente tra le firme dei metodi. Al termine della richiesta viene eseguito un `clear()` per prevenire memory leak.
* **Controllo degli Accessi:** Distinzione netta tra due ruoli operativi:
  * **Seller (Venditore):** Focalizzato sull'esplorazione del catalogo, schede dettaglio e raccomandazione di veicoli simili.
  * **Analyst (Analista):** Focalizzato su analisi statistiche trasversali, calcolo dei prezzi medi, deprezzamento e impatto del chilometraggio.

---

## 🛠️ Tecnologie Utilizzate

* **Linguaggio:** Java 17+
* **Framework:** Spring Boot (Web, Data JPA)
* **Database:** PostgreSQL (con driver JDBC)
* **Template Engine:** Thymeleaf
* **Gestione Dipendenze:** Maven
* **Sicurezza:** JJWT (JSON Web Token)
