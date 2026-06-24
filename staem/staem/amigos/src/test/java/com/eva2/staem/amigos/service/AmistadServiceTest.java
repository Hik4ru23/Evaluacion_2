package com.eva2.staem.amigos.service;

import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.dto.AmistadResponseDTO;
import com.eva2.staem.amigos.model.Amistad;
import com.eva2.staem.amigos.repository.AmistadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmistadServiceTest {

    @Mock
    private AmistadRepository amistadRepository;

    @InjectMocks
    private AmistadService amistadService;

    @Test
    void enviarSolicitud_NoExistePreviamente_GuardaEnEstadoPendiente() {
        AmistadRequestDTO request = new AmistadRequestDTO();
        request.setUsuarioId(1L);
        request.setAmigoId(2L);

        Amistad amistadGuardada = Amistad.builder()
                .id(100L)
                .usuarioId(1L)
                .amigoId(2L)
                .estado("PENDIENTE")
                .build();

        when(amistadRepository.existsByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(false);
        when(amistadRepository.save(any(Amistad.class))).thenReturn(amistadGuardada);

        AmistadResponseDTO response = amistadService.enviarSolicitud(request);

        assertNotNull(response);
        assertEquals("PENDIENTE", response.getEstado());
        verify(amistadRepository, times(1)).save(any(Amistad.class));
    }

    @Test
    void responderSolicitud_AccionRechazar_EliminaRegistroYRetornaNull() {
        Long usuarioId = 1L;
        Long amigoId = 2L;
        Amistad amistadPendiente = Amistad.builder()
                .id(100L)
                .usuarioId(usuarioId)
                .amigoId(amigoId)
                .estado("PENDIENTE")
                .build();

        when(amistadRepository.findByUsuarioIdAndAmigoId(usuarioId, amigoId))
                .thenReturn(Optional.of(amistadPendiente));

        AmistadResponseDTO response = amistadService.responderSolicitud(usuarioId, amigoId, "RECHAZAR");

        assertNull(response);
        verify(amistadRepository, times(1)).delete(amistadPendiente);
        verify(amistadRepository, never()).save(any(Amistad.class));
    }

    @Test
    void enviarSolicitud_YaExiste_LanzaExcepcion() {
        AmistadRequestDTO request = new AmistadRequestDTO();
        request.setUsuarioId(1L);
        request.setAmigoId(2L);

        when(amistadRepository.existsByUsuarioIdAndAmigoId(1L, 2L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            amistadService.enviarSolicitud(request);
        });
        
        assertEquals("La solicitud de amistad ya existe o ya son amigos.", exception.getMessage());
        verify(amistadRepository, never()).save(any(Amistad.class));
    }
}
