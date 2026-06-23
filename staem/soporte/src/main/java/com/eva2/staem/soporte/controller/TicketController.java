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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/soporte")
@Tag(name = "Soporte", description = "Endpoints para la gestión de tickets de soporte técnico")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/crear")
    @Operation(summary = "Crear nuevo ticket", description = "Permite a un usuario crear un ticket de soporte nuevo.")
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
    @Operation(summary = "Obtener tickets por usuario", description = "Devuelve el listado completo de tickets creados por un usuario específico.")
    public ResponseEntity<?> obtenerTicketsPorUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(ticketService.obtenerTicketsPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cerrar/{ticketId}")
    @Operation(summary = "Cerrar un ticket", description = "Permite cerrar un ticket de soporte existente, marcándolo como resuelto.")
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