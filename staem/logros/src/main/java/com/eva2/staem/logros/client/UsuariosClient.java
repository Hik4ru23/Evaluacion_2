package com.eva2.staem.logros.client;

import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuariosClient {

    private final WebClient webClient;

    public UsuariosClient(WebClient.Builder webClientBuilder, @Value("${ms.usuarios.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl + "/api/usuarios").build();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .block();
    }

    public UsuarioResponseDTO buscarPorCorreo(String correo) {
        return this.webClient.get()
                .uri("/correo/{correo}", correo)
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .block();
    }

    public UsuarioResponseDTO descontarSaldo(Long id, Double monto) {
        return this.webClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/{id}/descontar")
                        .queryParam("monto", monto)
                        .build(id))
                .retrieve()
                .bodyToMono(UsuarioResponseDTO.class)
                .block();
    }
}
