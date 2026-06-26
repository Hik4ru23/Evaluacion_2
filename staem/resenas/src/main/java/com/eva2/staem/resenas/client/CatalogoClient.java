package com.eva2.staem.resenas.client;
import com.eva2.staem.resenas.dto.JuegoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
@Component
public class CatalogoClient {
    private final WebClient webClient;
    public CatalogoClient(WebClient.Builder webClientBuilder, @Value("${ms.catalogo.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl + "/api/catalogo").build();
    }
    public JuegoResponseDTO buscarPorId(Long id) {
        return this.webClient.get().uri("/{id}", id).retrieve().bodyToMono(JuegoResponseDTO.class).block();
    }
}
