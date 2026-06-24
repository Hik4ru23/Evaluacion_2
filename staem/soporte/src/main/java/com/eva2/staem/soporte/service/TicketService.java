package com.eva2.staem.soporte.service;

import com.eva2.staem.soporte.dto.TicketRequestDTO;
import com.eva2.staem.soporte.dto.TicketResponseDTO;
import com.eva2.staem.soporte.model.Ticket;
import com.eva2.staem.soporte.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketResponseDTO crearTicket(TicketRequestDTO request) {
        Ticket ticket = Ticket.builder()
                .usuarioId(request.getUsuarioId())
                .asunto(request.getAsunto())
                .descripcion(request.getDescripcion())
                .build();

        Ticket guardado = ticketRepository.save(ticket);
        return mapearAResponse(guardado);
    }

    public List<TicketResponseDTO> obtenerTicketsPorUsuario(Long usuarioId) {
        List<Ticket> tickets = ticketRepository.findByUsuarioId(usuarioId);
        return tickets.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    public TicketResponseDTO cerrarTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        
        ticket.setEstado("CERRADO");
        Ticket actualizado = ticketRepository.save(ticket);
        return mapearAResponse(actualizado);
    }

    private TicketResponseDTO mapearAResponse(Ticket ticket) {
        if (ticket == null) return null;
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .usuarioId(ticket.getUsuarioId())
                .asunto(ticket.getAsunto())
                .descripcion(ticket.getDescripcion())
                .estado(ticket.getEstado())
                .fechaCreacion(ticket.getFechaCreacion())
                .build();
    }
}   
