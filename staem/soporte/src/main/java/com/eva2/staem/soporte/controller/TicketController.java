package com.eva2.staem.soporte.controller;

import com.eva2.staem.soporte.dto.TicketRequestDTO;
import com.eva2.staem.soporte.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/soporte")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearTicket(@Valid @RequestBody TicketRequestDTO request) {
        try {
            return ResponseEntity.ok(ticketService.crearTicket(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerTicketsPorUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(ticketService.obtenerTicketsPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cerrar/{ticketId}")
    public ResponseEntity<?> cerrarTicket(@PathVariable Long ticketId) {
        try {
            return ResponseEntity.ok(ticketService.cerrarTicket(ticketId));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", status.is4xxClientError() ? "Validación" : "Error interno");
        body.put("message", ex.getMessage() == null ? "Ocurrió un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}