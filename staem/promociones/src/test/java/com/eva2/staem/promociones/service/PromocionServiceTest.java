package com.eva2.staem.promociones.service;

import com.eva2.staem.promociones.dto.PromocionRequestDTO;
import com.eva2.staem.promociones.dto.PromocionResponseDTO;
import com.eva2.staem.promociones.model.Promocion;
import com.eva2.staem.promociones.repository.PromocionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    @Mock
    private com.eva2.staem.promociones.client.CatalogoClient catalogoClient;

    @org.junit.jupiter.api.BeforeEach
    void setUpMocks() {
        org.mockito.Mockito.lenient().when(catalogoClient.buscarPorId(org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(new com.eva2.staem.promociones.dto.JuegoResponseDTO());
    }

    @Test
    void crearPromocion_FlujoExitoso_RetornaPromocionResponseDTO() {
        PromocionRequestDTO request = new PromocionRequestDTO();
        request.setJuegoId(10L);
        request.setPorcentajeDescuento(50.0);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusDays(7));

        Promocion promocionSimulada = Promocion.builder()
                .id(1L)
                .juegoId(10L)
                .porcentajeDescuento(50.0)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionSimulada);

        PromocionResponseDTO response = promocionService.crearPromocion(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(50.0, response.getPorcentajeDescuento());
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }

    @Test
    void obtenerPromocionesPorJuego_ConDatos_RetornaListaDeDTOs() {
        Long juegoId = 10L;
        Promocion promo1 = Promocion.builder().id(1L).juegoId(juegoId).porcentajeDescuento(20.0).build();
        Promocion promo2 = Promocion.builder().id(2L).juegoId(juegoId).porcentajeDescuento(50.0).build();
        
        when(promocionRepository.findByJuegoId(juegoId)).thenReturn(Arrays.asList(promo1, promo2));

        List<PromocionResponseDTO> resultado = promocionService.obtenerPromocionesPorJuego(juegoId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(promocionRepository, times(1)).findByJuegoId(juegoId);
    }
}
