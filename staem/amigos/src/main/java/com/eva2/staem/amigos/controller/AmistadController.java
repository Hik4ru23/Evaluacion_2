package com.eva2.staem.amigos.controller;

import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.service.AmistadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/amigos")
@Tag(name = "Gestión de Amigos", description = "Endpoints para el manejo de relaciones sociales, solicitudes de amistad y listado de amigos")
public class AmistadController {
    
    @Autowired
    private AmistadService amistadService;

    @Operation(summary = "Enviar solicitud de amistad", description = "Crea una nueva relación en estado 'PENDIENTE' entre dos usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud enviada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La solicitud ya existe o datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/solicitar")
    public ResponseEntity<?> enviarSolicitud(@Valid @RequestBody AmistadRequestDTO request) {
        try {
            return ResponseEntity.ok(amistadService.enviarSolicitud(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Responder a una solicitud", description = "Permite ACEPTAR (cambia el estado) o RECHAZAR (elimina el registro) una solicitud de amistad pendiente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta procesada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud no encontrada o acción inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/responder/{usuarioId}/{amigoId}")
    public ResponseEntity<?> responderSolicitud(
            @PathVariable Long usuarioId,
            @PathVariable Long amigoId,
            @RequestParam String accion) {
        try {
            return ResponseEntity.ok(amistadService.responderSolicitud(usuarioId, amigoId, accion));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener lista de amigos", description = "Devuelve únicamente las relaciones en estado 'ACEPTADA' para un usuario específico.")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerAmigos(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(amistadService.obtenerAmigos(usuarioId));
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