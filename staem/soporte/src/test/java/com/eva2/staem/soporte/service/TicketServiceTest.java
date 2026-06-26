package com.eva2.staem.soporte.service;

import com.eva2.staem.soporte.dto.TicketRequestDTO;
import com.eva2.staem.soporte.dto.TicketResponseDTO;
import com.eva2.staem.soporte.model.Ticket;
import com.eva2.staem.soporte.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private com.eva2.staem.soporte.client.UsuariosClient usuariosClient;

    private Ticket ticket1;
    private Ticket ticket2;
    private TicketRequestDTO ticketRequestDTO;

    @BeforeEach
    void setUp() {
        ticket1 = Ticket.builder()
                .id(1L)
                .usuarioId(100L)
                .asunto("Asunto 1")
                .descripcion("Descripción 1")
                .estado("ABIERTO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        ticket2 = Ticket.builder()
                .id(2L)
                .usuarioId(100L)
                .asunto("Asunto 2")
                .descripcion("Descripción 2")
                .estado("CERRADO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setUsuarioId(100L);
        ticketRequestDTO.setAsunto("Asunto Nuevo");
        ticketRequestDTO.setDescripcion("Descripción Nueva");

        org.mockito.Mockito.lenient().when(usuariosClient.buscarPorId(org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(new com.eva2.staem.soporte.dto.UsuarioResponseDTO());
    }

    @Test
    void givenValidRequest_whenCrearTicket_thenReturnsTicketResponseDTO() {
        Ticket savedTicket = Ticket.builder()
                .id(3L)
                .usuarioId(100L)
                .asunto("Asunto Nuevo")
                .descripcion("Descripción Nueva")
                .estado("ABIERTO")
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketResponseDTO response = ticketService.crearTicket(ticketRequestDTO);

        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("Asunto Nuevo", response.getAsunto());
        assertEquals("Descripción Nueva", response.getDescripcion());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void givenUsuarioId_whenObtenerTicketsPorUsuario_thenReturnsListOfTickets() {
        Long usuarioId = 100L;
        when(ticketRepository.findByUsuarioId(usuarioId)).thenReturn(Arrays.asList(ticket1, ticket2));

        List<TicketResponseDTO> responses = ticketService.obtenerTicketsPorUsuario(usuarioId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Asunto 1", responses.get(0).getAsunto());
        assertEquals("Asunto 2", responses.get(1).getAsunto());
        verify(ticketRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    void givenValidTicketId_whenCerrarTicket_thenReturnsClosedTicket() {
        Long ticketId = 1L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket1));
        
        Ticket updatedTicket = Ticket.builder()
                .id(1L)
                .usuarioId(100L)
                .asunto("Asunto 1")
                .descripcion("Descripción 1")
                .estado("CERRADO")
                .fechaCreacion(ticket1.getFechaCreacion())
                .build();
                
        when(ticketRepository.save(any(Ticket.class))).thenReturn(updatedTicket);

        TicketResponseDTO response = ticketService.cerrarTicket(ticketId);

        assertNotNull(response);
        assertEquals("CERRADO", response.getEstado());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void givenInvalidTicketId_whenCerrarTicket_thenThrowsRuntimeException() {
        Long ticketId = 99L;
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ticketService.cerrarTicket(ticketId));
        assertEquals("Ticket no encontrado", exception.getMessage());
        verify(ticketRepository, times(1)).findById(ticketId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
