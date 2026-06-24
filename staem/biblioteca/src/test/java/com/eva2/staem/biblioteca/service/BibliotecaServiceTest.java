package com.eva2.staem.biblioteca.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.biblioteca.dto.BibliotecaResponseDTO;
import com.eva2.staem.biblioteca.model.Biblioteca;
import com.eva2.staem.biblioteca.repository.BibliotecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BibliotecaService - Unit Tests")
class BibliotecaServiceTest {

    @Mock
    private BibliotecaRepository bibliotecaRepository;

    @InjectMocks
    private BibliotecaService bibliotecaService;

    @Test
    @DisplayName("1. agregarJuegos exitoso con un juego nuevo")
    void agregarJuegos_exitoso_juegoNuevo() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(1L)
                .juegosIds(List.of(100L))
                .build();

        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 100L)).thenReturn(Optional.empty());

        List<Biblioteca> guardados = List.of(
                Biblioteca.builder().id(1L).usuarioId(1L).juegoId(100L).fechaAdquisicion(LocalDateTime.now()).build()
        );
        when(bibliotecaRepository.saveAll(anyList())).thenReturn(guardados);

        List<BibliotecaResponseDTO> response = bibliotecaService.agregarJuegos(request);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getJuegoId()).isEqualTo(100L);
        verify(bibliotecaRepository).findByUsuarioIdAndJuegoId(1L, 100L);
        verify(bibliotecaRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("2. agregarJuegos filtra juegos duplicados (ya existentes en biblioteca)")
    void agregarJuegos_filtraDuplicados() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(1L)
                .juegosIds(List.of(100L, 101L))
                .build();

        Biblioteca existente = Biblioteca.builder().id(1L).usuarioId(1L).juegoId(100L).build();
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 100L)).thenReturn(Optional.of(existente));
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 101L)).thenReturn(Optional.empty());

        List<Biblioteca> guardados = List.of(
                Biblioteca.builder().id(2L).usuarioId(1L).juegoId(101L).build()
        );
        when(bibliotecaRepository.saveAll(anyList())).thenReturn(guardados);

        List<BibliotecaResponseDTO> response = bibliotecaService.agregarJuegos(request);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getJuegoId()).isEqualTo(101L);
        verify(bibliotecaRepository).findByUsuarioIdAndJuegoId(1L, 100L);
        verify(bibliotecaRepository).findByUsuarioIdAndJuegoId(1L, 101L);
    }

    @Test
    @DisplayName("3. agregarJuegos con usuarioId null lanza IllegalArgumentException")
    void agregarJuegos_usuarioIdNull_lanzaExcepcion() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(null)
                .juegosIds(List.of(100L))
                .build();

        assertThatThrownBy(() -> bibliotecaService.agregarJuegos(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El ID del usuario es obligatorio");

        verifyNoInteractions(bibliotecaRepository);
    }

    @Test
    @DisplayName("4. agregarJuegos con lista vacia lanza IllegalArgumentException")
    void agregarJuegos_listaVacia_lanzaExcepcion() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(1L)
                .juegosIds(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> bibliotecaService.agregarJuegos(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Debe enviar al menos un juego para agregar");

        verifyNoInteractions(bibliotecaRepository);
    }

    @Test
    @DisplayName("5. agregarJuegos todos los juegos ya existen - guarda lista vacia")
    void agregarJuegos_todosExisten_guardaListaVacia() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(1L)
                .juegosIds(List.of(100L))
                .build();

        Biblioteca existente = Biblioteca.builder().id(1L).usuarioId(1L).juegoId(100L).build();
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(1L, 100L)).thenReturn(Optional.of(existente));
        when(bibliotecaRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        List<BibliotecaResponseDTO> response = bibliotecaService.agregarJuegos(request);

        assertThat(response).isEmpty();
        verify(bibliotecaRepository).saveAll(argThat(iter -> !iter.iterator().hasNext()));
    }

    @Test
    @DisplayName("6. listarPorUsuario retorna lista de juegos del usuario")
    void listarPorUsuario_conJuegos_retornaLista() {
        List<Biblioteca> juegos = List.of(
                Biblioteca.builder().id(1L).usuarioId(1L).juegoId(100L).build(),
                Biblioteca.builder().id(2L).usuarioId(1L).juegoId(101L).build()
        );
        when(bibliotecaRepository.findByUsuarioId(1L)).thenReturn(juegos);

        List<BibliotecaResponseDTO> response = bibliotecaService.listarPorUsuario(1L);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getJuegoId()).isEqualTo(100L);
        assertThat(response.get(1).getJuegoId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("7. listarPorUsuario retorna lista vacia si no tiene juegos")
    void listarPorUsuario_sinJuegos_retornaListaVacia() {
        when(bibliotecaRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

        List<BibliotecaResponseDTO> response = bibliotecaService.listarPorUsuario(1L);

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("8. agregarJuegos con multiples juegos nuevos guarda todos")
    void agregarJuegos_multiplesNuevos_guardaTodos() {
        BibliotecaRequestDTO request = BibliotecaRequestDTO.builder()
                .usuarioId(1L)
                .juegosIds(List.of(100L, 101L, 102L))
                .build();

        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(eq(1L), anyLong())).thenReturn(Optional.empty());

        List<Biblioteca> guardados = List.of(
                Biblioteca.builder().id(1L).usuarioId(1L).juegoId(100L).build(),
                Biblioteca.builder().id(2L).usuarioId(1L).juegoId(101L).build(),
                Biblioteca.builder().id(3L).usuarioId(1L).juegoId(102L).build()
        );
        when(bibliotecaRepository.saveAll(anyList())).thenReturn(guardados);

        List<BibliotecaResponseDTO> response = bibliotecaService.agregarJuegos(request);

        assertThat(response).hasSize(3);
        verify(bibliotecaRepository, times(3)).findByUsuarioIdAndJuegoId(eq(1L), anyLong());
        verify(bibliotecaRepository).saveAll(argThat(iter -> ((java.util.Collection<?>) iter).size() == 3));
    }
}
