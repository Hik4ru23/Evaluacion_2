package com.eva2.staem.biblioteca.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.biblioteca.dto.BibliotecaResponseDTO;
import com.eva2.staem.biblioteca.model.Biblioteca;
import com.eva2.staem.biblioteca.repository.BibliotecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BibliotecaServiceTest {

    @Mock
    private BibliotecaRepository bibliotecaRepository;

    @InjectMocks
    private BibliotecaService bibliotecaService;

    @Test
    void agregarJuegos_FlujoExitoso_FiltraDuplicadosYGuarda() {
        BibliotecaRequestDTO request = new BibliotecaRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegosIds(Arrays.asList(10L, 20L));

        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(Optional.of(new Biblioteca()));
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 20L)).thenReturn(Optional.empty());

        Biblioteca juegoGuardado = Biblioteca.builder()
                .id(100L)
                .usuarioId(1L)
                .juegoId(20L)
                .fechaAdquisicion(LocalDateTime.now())
                .build();

        when(bibliotecaRepository.saveAll(anyList())).thenReturn(Collections.singletonList(juegoGuardado));

        List<BibliotecaResponseDTO> response = bibliotecaService.agregarJuegos(request);

        assertNotNull(response);
        assertEquals(1, response.size()); // Solo debe haber guardado 1 (el que no era duplicado)
        assertEquals(20L, response.get(0).getJuegoId());
        verify(bibliotecaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void agregarJuegos_SinJuegos_LanzaExcepcion() {
        BibliotecaRequestDTO request = new BibliotecaRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegosIds(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bibliotecaService.agregarJuegos(request);
        });

        assertEquals("Debe enviar al menos un juego para agregar", exception.getMessage());
        verify(bibliotecaRepository, never()).saveAll(anyList());
    }
}
