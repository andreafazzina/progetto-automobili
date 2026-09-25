package com.progetto.client.proxy;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.progetto.client.session.SessionState;
import com.progetto.shared.dto.auth.AuthTokenDTO;
import com.progetto.shared.dto.auth.CredentialsDTO;
import com.progetto.shared.dto.catalog.BrandDTO;
import com.progetto.shared.dto.catalog.CatalogPageDTO;
import com.progetto.shared.dto.catalog.CatalogQueryDTO;
import com.progetto.shared.dto.catalog.CarDetailDTO;
import com.progetto.shared.dto.analysis.AnalysisRequestDTO;
import com.progetto.shared.dto.analysis.AnalysisResultDTO;
import com.progetto.shared.dto.batch.BatchRequestDTO;
import com.progetto.shared.dto.batch.BatchResponseDTO;
import com.progetto.shared.service.AutoService;

/**
 * Remote Proxy
 * Il Proxy agisce come un surrogato locale dell'interfaccia condivisa AutoService. 
 * Non crea l'istanza dell'oggetto reale — poiché la vera logica di dominio risiede sull'applicazione Server — 
 * ma si occupa di incapsulare tutta la complessità della comunicazione distribuita.
 */
@Component
public class AutoServiceProxy implements AutoService {

    private final RestClient restClient;
    private final SessionState session;

    public AutoServiceProxy(RestClient restClient, SessionState session) {
        this.restClient = restClient;
        this.session = session;
    }

    // ===================== AUTENTICAZIONE =====================

    @Override
    public AuthTokenDTO login(CredentialsDTO credentials) {
        AuthTokenDTO auth = restClient.post()
                .uri("/login")
                .body(credentials)
                .retrieve()
                .body(AuthTokenDTO.class);

        // Memorizza token e ruolo nella sessione
        session.authenticate(auth.getToken(), auth.getUsername(), auth.getRole());

        return auth;
    }

    // ===================== CATALOGO =====================

    @Override
    public List<BrandDTO> getBrands() {
        BrandDTO[] brands = restClient.get()
                .uri("/brands")
                .header("Authorization", "Bearer " + session.getToken())
                .retrieve()
                .body(BrandDTO[].class);

        return (brands != null) ? List.of(brands) : List.of();
    }

    @Override
    public CatalogPageDTO getCatalog(CatalogQueryDTO query) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/catalog/{make}");
                    uriBuilder.queryParam("page", query.getPage());

                    // Filtri: aggiunti solo se valorizzati
                    if (query.getBodyType() != null && !query.getBodyType().isBlank())
                        uriBuilder.queryParam("bodyType", query.getBodyType());
                    if (query.getFuelType() != null && !query.getFuelType().isBlank())
                        uriBuilder.queryParam("fuelType", query.getFuelType());
                    if (query.getYearFrom() != null)
                        uriBuilder.queryParam("yearFrom", query.getYearFrom());
                    if (query.getYearTo() != null)
                        uriBuilder.queryParam("yearTo", query.getYearTo());
                    if (query.getPriceFrom() != null)
                        uriBuilder.queryParam("priceFrom", query.getPriceFrom());
                    if (query.getPriceTo() != null)
                        uriBuilder.queryParam("priceTo", query.getPriceTo());
                    if (query.getMileageMax() != null)
                        uriBuilder.queryParam("mileageMax", query.getMileageMax());

                    // Ordinamento: solo se specificato
                    if (query.getSortBy() != null && !query.getSortBy().isBlank())
                        uriBuilder.queryParam("sortBy", query.getSortBy());
                    if (query.getSortDir() != null && !query.getSortDir().isBlank())
                        uriBuilder.queryParam("sortDir", query.getSortDir());

                    return uriBuilder.build(query.getMake());
                })
                .header("Authorization", "Bearer " + session.getToken())
                .retrieve()
                .body(CatalogPageDTO.class);
    }

    @Override
    public CarDetailDTO getCarDetail(long carId) {
        return restClient.get()
                .uri("/cars/{id}", carId)
                .header("Authorization", "Bearer " + session.getToken())
                .retrieve()
                .body(CarDetailDTO.class);
    }

    // ===================== ANALISI =====================

    @Override
    public AnalysisResultDTO runAnalysis(AnalysisRequestDTO request) {
        return restClient.post()
                .uri("/analysis")
                .header("Authorization", "Bearer " + session.getToken())
                .body(request)
                .retrieve()
                .body(AnalysisResultDTO.class);
    }

    // ===================== BATCH =====================

    @Override
    public BatchResponseDTO executeBatch(BatchRequestDTO batch) {
        return restClient.post()
                .uri("/batch")
                .header("Authorization", "Bearer " + session.getToken())
                .body(batch)
                .retrieve()
                .body(BatchResponseDTO.class);
    }
}