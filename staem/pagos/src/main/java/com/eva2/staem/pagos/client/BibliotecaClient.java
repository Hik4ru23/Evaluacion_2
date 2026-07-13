package com.eva2.staem.pagos.client;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BibliotecaClient {

    private final WebClient webClient;

    public BibliotecaClient(WebClient.Builder webClientBuilder, @Value("${ms.biblioteca.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl + "/api/biblioteca").build();
    }

    public List<Object> agregarJuegos(BibliotecaRequestDTO request) {
        return this.webClient.post()
                .uri("/agregar")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Object>>() {})
                .block();
    }
}
