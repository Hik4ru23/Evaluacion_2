package com.eva2.staem.soporte.client;
import com.eva2.staem.soporte.dto.UsuarioResponseDTO;
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
        return this.webClient.get().uri("/{id}", id).retrieve().bodyToMono(UsuarioResponseDTO.class).block();
    }
}
