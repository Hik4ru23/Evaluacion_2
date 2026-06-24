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
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket creado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 100,\n  \"asunto\": \"Problema de login\",\n  \"descripcion\": \"No puedo ingresar a mi cuenta\",\n  \"estado\": \"ABIERTO\",\n  \"fechaCreacion\": \"2023-10-10T12:00:00\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"error\": \"Validación\",\n  \"message\": \"El asunto es obligatorio\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearTicket(
            @io.swagger.v3.oas.annotations.Parameter(description = "Datos del ticket a crear", required = true)
            @Valid @RequestBody TicketRequestDTO request) {
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
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"usuarioId\": 100,\n    \"asunto\": \"Problema de login\",\n    \"descripcion\": \"No puedo ingresar a mi cuenta\",\n    \"estado\": \"ABIERTO\"\n  }\n]"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error al consultar base de datos\"\n}")))
    })
    public ResponseEntity<?> obtenerTicketsPorUsuario(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID del usuario cuyos tickets se desea obtener", example = "100")
            @PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(ticketService.obtenerTicketsPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cerrar/{ticketId}")
    @Operation(summary = "Cerrar un ticket", description = "Permite cerrar un ticket de soporte existente, marcándolo como resuelto.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket cerrado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 100,\n  \"asunto\": \"Problema de login\",\n  \"descripcion\": \"Resuelto por el agente\",\n  \"estado\": \"CERRADO\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error al intentar cerrar",
            content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"error\": \"Validación\",\n  \"message\": \"Ticket no encontrado\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> cerrarTicket(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID del ticket que se desea cerrar", example = "1")
            @PathVariable Long ticketId) {
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
