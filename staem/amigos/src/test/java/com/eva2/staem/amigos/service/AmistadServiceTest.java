package com.eva2.staem.amigos.service;

import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.dto.AmistadResponseDTO;
import com.eva2.staem.amigos.model.Amistad;
import com.eva2.staem.amigos.repository.AmistadRepository;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AmistadService - Unit Tests")
class AmistadServiceTest {

    @Mock
    private AmistadRepository amistadRepository;

    @InjectMocks
    private AmistadService amistadService;

    @Test
    @DisplayName("1. enviarSolicitud exitoso - debe guardar amistad con estado PENDIENTE")
    void enviarSolicitud_cuandoNoExisteSolicitud_debeGuardarConEstadoPendiente() {
        AmistadRequestDTO request = AmistadRequestDTO.builder()
                .usuarioId(1L)
                .amigoId(2L)
                .build();

        Amistad amistadGuardada = Amistad.builder()
                .id(10L)
                .usuarioId(1L)
                .amigoId(2L)
                .estado("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        when(amistadRepository.existsByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(false);
        when(amistadRepository.save(any(Amistad.class))).thenReturn(amistadGuardada);

        AmistadResponseDTO response = amistadService.enviarSolicitud(request);

        assertThat(response).isNotNull();
        assertThat(response.getEstado()).isEqualTo("PENDIENTE");
        assertThat(response.getUsuarioId()).isEqualTo(1L);
        assertThat(response.getAmigoId()).isEqualTo(2L);
        verify(amistadRepository).existsByUsuarioIdAndAmigoId(1L, 2L);
        verify(amistadRepository).save(any(Amistad.class));
    }

    @Test
    @DisplayName("2. enviarSolicitud cuando solicitud ya existe - debe lanzar RuntimeException")
    void enviarSolicitud_cuandoSolicitudYaExiste_debeLanzarRuntimeException() {
        AmistadRequestDTO request = AmistadRequestDTO.builder()
                .usuarioId(1L)
                .amigoId(2L)
                .build();

        when(amistadRepository.existsByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> amistadService.enviarSolicitud(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La solicitud de amistad ya existe o ya son amigos.");

        verify(amistadRepository).existsByUsuarioIdAndAmigoId(1L, 2L);
        verify(amistadRepository, never()).save(any(Amistad.class));
    }

    @Test
    @DisplayName("3. responderSolicitud ACEPTAR - debe actualizar estado a ACEPTADA")
    void responderSolicitud_cuandoAccion_ACEPTAR_debeActualizarEstadoAceptada() {
        Amistad amistad = Amistad.builder()
                .id(10L)
                .usuarioId(1L)
                .amigoId(2L)
                .estado("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        Amistad amistadActualizada = Amistad.builder()
                .id(10L)
                .usuarioId(1L)
                .amigoId(2L)
                .estado("ACEPTADA")
                .fechaSolicitud(amistad.getFechaSolicitud())
                .build();

        when(amistadRepository.findByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(Optional.of(amistad));
        when(amistadRepository.save(amistad)).thenReturn(amistadActualizada);

        AmistadResponseDTO response = amistadService.responderSolicitud(1L, 2L, "ACEPTAR");

        assertThat(response).isNotNull();
        assertThat(response.getEstado()).isEqualTo("ACEPTADA");
        verify(amistadRepository).save(amistad);
        verify(amistadRepository, never()).delete(any(Amistad.class));
    }

    @Test
    @DisplayName("4. responderSolicitud RECHAZAR - debe eliminar la amistad y retornar null")
    void responderSolicitud_cuandoAccion_RECHAZAR_debeEliminarYRetornarNull() {
        Amistad amistad = Amistad.builder()
                .id(10L)
                .usuarioId(1L)
                .amigoId(2L)
                .estado("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        when(amistadRepository.findByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(Optional.of(amistad));
        doNothing().when(amistadRepository).delete(amistad);

        AmistadResponseDTO response = amistadService.responderSolicitud(1L, 2L, "RECHAZAR");

        assertThat(response).isNull();
        verify(amistadRepository).delete(amistad);
        verify(amistadRepository, never()).save(any(Amistad.class));
    }

    @Test
    @DisplayName("5. responderSolicitud solicitud no encontrada - debe lanzar RuntimeException")
    void responderSolicitud_cuandoSolicitudNoExiste_debeLanzarRuntimeException() {
        when(amistadRepository.findByUsuarioIdAndAmigoId(99L, 100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> amistadService.responderSolicitud(99L, 100L, "ACEPTAR"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solicitud no encontrada");

        verify(amistadRepository).findByUsuarioIdAndAmigoId(99L, 100L);
        verify(amistadRepository, never()).save(any(Amistad.class));
        verify(amistadRepository, never()).delete(any(Amistad.class));
    }

    @Test
    @DisplayName("6. obtenerAmigos - debe retornar lista de amistades con estado ACEPTADA")
    void obtenerAmigos_cuandoExistenAmigos_debeRetornarLista() {
        Long usuarioId = 1L;
        LocalDateTime ahora = LocalDateTime.now();

        List<Amistad> amistades = List.of(
                Amistad.builder().id(1L).usuarioId(usuarioId).amigoId(2L).estado("ACEPTADA").fechaSolicitud(ahora).build(),
                Amistad.builder().id(2L).usuarioId(usuarioId).amigoId(3L).estado("ACEPTADA").fechaSolicitud(ahora).build()
        );

        when(amistadRepository.findByUsuarioIdAndEstado(usuarioId, "ACEPTADA")).thenReturn(amistades);

        List<AmistadResponseDTO> resultado = amistadService.obtenerAmigos(usuarioId);

        assertThat(resultado).isNotNull().hasSize(2);
        assertThat(resultado).allMatch(dto -> "ACEPTADA".equals(dto.getEstado()));
        verify(amistadRepository).findByUsuarioIdAndEstado(usuarioId, "ACEPTADA");
    }

    @Test
    @DisplayName("7. obtenerAmigos - debe retornar lista vacía cuando no hay amigos")
    void obtenerAmigos_cuandoNoExistenAmigos_debeRetornarListaVacia() {
        Long usuarioId = 1L;
        when(amistadRepository.findByUsuarioIdAndEstado(usuarioId, "ACEPTADA"))
                .thenReturn(Collections.emptyList());

        List<AmistadResponseDTO> resultado = amistadService.obtenerAmigos(usuarioId);

        assertThat(resultado).isNotNull().isEmpty();
        verify(amistadRepository).findByUsuarioIdAndEstado(usuarioId, "ACEPTADA");
    }

    @Test
    @DisplayName("8. responderSolicitud ACEPTAR - debe llamar save con estado ACEPTADA")
    void responderSolicitud_ACEPTAR_debeLlamarSaveConEstadoAceptada() {
        Amistad amistad = Amistad.builder()
                .id(5L)
                .usuarioId(10L)
                .amigoId(20L)
                .estado("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        Amistad amistadAceptada = Amistad.builder()
                .id(5L)
                .usuarioId(10L)
                .amigoId(20L)
                .estado("ACEPTADA")
                .fechaSolicitud(amistad.getFechaSolicitud())
                .build();

        when(amistadRepository.findByUsuarioIdAndAmigoId(10L, 20L)).thenReturn(Optional.of(amistad));
        when(amistadRepository.save(any(Amistad.class))).thenReturn(amistadAceptada);

        AmistadResponseDTO response = amistadService.responderSolicitud(10L, 20L, "ACEPTAR");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getEstado()).isEqualTo("ACEPTADA");

        verify(amistadRepository).save(argThat(a -> "ACEPTADA".equals(a.getEstado())));
    }
}
