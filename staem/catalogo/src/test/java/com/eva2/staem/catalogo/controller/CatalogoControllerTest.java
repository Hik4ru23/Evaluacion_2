package com.eva2.staem.catalogo.controller;

import com.eva2.staem.catalogo.dto.JuegoRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.catalogo.service.CatalogoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoControllerTest {

    @Mock
    private CatalogoService catalogoService;

    @InjectMocks
    private CatalogoController catalogoController;

    private JuegoRequestDTO requestDTO;
    private JuegoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = JuegoRequestDTO.builder()
                .titulo("Zelda")
                .descripcion("Juego")
                .precio(59.99)
                .genero("Aventura")
                .desarrollador("Nintendo")
                .imagenUrl("img")
                .stock(10)
                .build();

        responseDTO = JuegoResponseDTO.builder()
                .id(1L)
                .titulo("Zelda")
                .precio(59.99)
                .stock(10)
                .disponible(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/catalogo - Exito")
    void agregarJuego() {
        when(catalogoService.agregarJuego(any(JuegoRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<?> response = catalogoController.agregarJuego(requestDTO);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("GET /api/catalogo - Listar")
    void listarJuegos() {
        when(catalogoService.listarJuegos()).thenReturn(List.of(responseDTO));

        ResponseEntity<?> response = catalogoController.listarJuegos();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("GET /api/catalogo/{id} - Exito")
    void buscarPorId_exito() {
        when(catalogoService.buscarPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<?> response = catalogoController.buscarPorId(1L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("DELETE /api/catalogo/{id} - Exito")
    void eliminarJuego() {
        doNothing().when(catalogoService).eliminarJuego(1L);

        ResponseEntity<?> response = catalogoController.eliminarJuego(1L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("PATCH /api/catalogo/{id}/stock - Exito")
    void descontarStock() {
        responseDTO.setStock(5);
        when(catalogoService.descontarStock(1L, 5)).thenReturn(responseDTO);

        ResponseEntity<?> response = catalogoController.descontarStock(1L, 5);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }
}
