package com.eva2.staem.resenas.service;

import com.eva2.staem.resenas.dto.ResenaRequestDTO;
import com.eva2.staem.resenas.dto.ResenaResponseDTO;
import com.eva2.staem.resenas.model.Resena;
import com.eva2.staem.resenas.repository.ResenaRepository;
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
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private com.eva2.staem.resenas.client.UsuariosClient usuariosClient;

    @Mock
    private com.eva2.staem.resenas.client.CatalogoClient catalogoClient;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void crearResena_FlujoExitoso_RetornaResenaResponseDTO() {
        ResenaRequestDTO request = new ResenaRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegoId(10L);
        request.setCalificacion(5);
        request.setComentario("Juego con mecánicas increíbles");

        Resena resenaSimulada = Resena.builder()
                .id(100L)
                .usuarioId(1L)
                .juegoId(10L)
                .calificacion(5)
                .comentario("Juego con mecánicas increíbles")
                .fechaResena(LocalDateTime.now())
                .build();

        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaSimulada);
        when(usuariosClient.buscarPorId(1L)).thenReturn(new com.eva2.staem.resenas.dto.UsuarioResponseDTO());
        when(catalogoClient.buscarPorId(10L)).thenReturn(new com.eva2.staem.resenas.dto.JuegoResponseDTO());

        ResenaResponseDTO response = resenaService.crearResena(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(5, response.getCalificacion());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void obtenerResenasPorJuego_ConDatos_RetornaListaDeDTOs() {
        Long juegoId = 10L;
        Resena resena1 = Resena.builder().id(1L).juegoId(juegoId).calificacion(4).build();
        Resena resena2 = Resena.builder().id(2L).juegoId(juegoId).calificacion(5).build();
        
        when(resenaRepository.findByJuegoId(juegoId)).thenReturn(Arrays.asList(resena1, resena2));
        when(catalogoClient.buscarPorId(10L)).thenReturn(new com.eva2.staem.resenas.dto.JuegoResponseDTO());

        List<ResenaResponseDTO> resultado = resenaService.obtenerResenasPorJuego(juegoId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(resenaRepository, times(1)).findByJuegoId(juegoId);
    }

    @Test
    void obtenerResenasPorUsuario_ConDatos_RetornaListaDeDTOs() {
        Long usuarioId = 1L;
        Resena resena1 = Resena.builder().id(1L).usuarioId(usuarioId).juegoId(10L).calificacion(4).build();
        Resena resena2 = Resena.builder().id(2L).usuarioId(usuarioId).juegoId(11L).calificacion(5).build();
        
        when(resenaRepository.findByUsuarioId(usuarioId)).thenReturn(Arrays.asList(resena1, resena2));
        when(usuariosClient.buscarPorId(1L)).thenReturn(new com.eva2.staem.resenas.dto.UsuarioResponseDTO());

        List<ResenaResponseDTO> resultado = resenaService.obtenerResenasPorUsuario(usuarioId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(resenaRepository, times(1)).findByUsuarioId(usuarioId);
    }
}
