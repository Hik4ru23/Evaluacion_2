package com.eva2.staem.pagos.controller;

import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.dto.PagoResponseDTO;
import com.eva2.staem.pagos.service.PagosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagosControllerTest {

    @Mock
    private PagosService pagosService;

    @InjectMocks
    private PagosController pagosController;

    private CompraRequestDTO requestDTO;
    private PagoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = CompraRequestDTO.builder()
                .juegosIds(List.of(1L, 2L))
                .build();

        responseDTO = PagoResponseDTO.builder()
                .id(100L)
                .usuarioId(1L)
                .totalPagado(100.0)
                .fechaTransaccion(LocalDateTime.now())
                .estado("Aprobado")
                .build();
    }

    @Test
    @DisplayName("POST /api/pagos/comprar - Exito")
    void comprarJuegos_exito() {
        when(pagosService.procesarCompra(eq("test@mail.com"), any(CompraRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<?> response = pagosController.comprarJuegos("test@mail.com", requestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }
}
