package com.eva2.staem.soporte.service;

import com.eva2.staem.soporte.dto.TicketRequestDTO;
import com.eva2.staem.soporte.dto.TicketResponseDTO;
import com.eva2.staem.soporte.model.Ticket;
import com.eva2.staem.soporte.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void crearTicket_FlujoExitoso_RetornaTicketResponseDTO() {
        TicketRequestDTO request = new TicketRequestDTO();
        request.setUsuarioId(1L);
        request.setAsunto("Problema de conexión");
        request.setDescripcion("No puedo entrar al servidor");

        Ticket ticketSimulado = Ticket.builder()
                .id(100L)
                .usuarioId(1L)
                .asunto("Problema de conexión")
                .descripcion("No puedo entrar al servidor")
                .estado("ABIERTO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketSimulado);
        TicketResponseDTO response = ticketService.crearTicket(request);
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("ABIERTO", response.getEstado());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void cerrarTicket_TicketExiste_CambiaEstadoYRetornaDTO() {
        Long ticketId = 50L;
        Ticket ticketActivo = Ticket.builder()
                .id(ticketId)
                .estado("ABIERTO")
                .build();
        
        Ticket ticketCerrado = Ticket.builder()
                .id(ticketId)
                .estado("CERRADO")
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticketActivo));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketCerrado);

        TicketResponseDTO response = ticketService.cerrarTicket(ticketId);

        assertNotNull(response);
        assertEquals("CERRADO", response.getEstado());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void cerrarTicket_TicketNoExiste_LanzaExcepcion() {
        Long ticketId = 99L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.cerrarTicket(ticketId);
        });
        
        assertEquals("Ticket no encontrado", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}