package com.eva2.staem.amigos.controller;

import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.service.AmistadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            @ApiResponse(responseCode = "200", description = "Solicitud enviada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 100,\n  \"amigoId\": 200,\n  \"estado\": \"PENDIENTE\",\n  \"fechaSolicitud\": \"2023-10-10T12:00:00\"\n}"))),
            @ApiResponse(responseCode = "400", description = "La solicitud ya existe o datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"La solicitud de amistad ya existe o ya son amigos.\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @PostMapping("/solicitar")
    public ResponseEntity<?> enviarSolicitud(
            @Parameter(description = "Datos de la solicitud de amistad", required = true)
            @Valid @RequestBody AmistadRequestDTO request) {
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
            @ApiResponse(responseCode = "200", description = "Respuesta procesada correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 100,\n  \"amigoId\": 200,\n  \"estado\": \"ACEPTADA\",\n  \"fechaSolicitud\": \"2023-10-10T12:00:00\"\n}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud no encontrada o acción inválida",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"Solicitud no encontrada\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @PutMapping("/responder/{usuarioId}/{amigoId}")
    public ResponseEntity<?> responderSolicitud(
            @Parameter(description = "ID del usuario que responde a la solicitud", required = true, example = "100") @PathVariable Long usuarioId,
            @Parameter(description = "ID del amigo que envió la solicitud", required = true, example = "200") @PathVariable Long amigoId,
            @Parameter(description = "Acción a realizar: ACEPTAR o RECHAZAR", required = true, example = "ACEPTAR") @RequestParam String accion) {
        try {
            return ResponseEntity.ok(amistadService.responderSolicitud(usuarioId, amigoId, accion));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener lista de amigos", description = "Devuelve únicamente las relaciones en estado 'ACEPTADA' para un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"usuarioId\": 100,\n    \"amigoId\": 200,\n    \"estado\": \"ACEPTADA\",\n    \"fechaSolicitud\": \"2023-10-10T12:00:00\"\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerAmigos(
            @Parameter(description = "ID del usuario para obtener sus amigos", required = true, example = "100") @PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(amistadService.obtenerAmigos(usuarioId));
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", status.is4xxClientError() ? "Validacion" : "Error interno");
        body.put("message", ex.getMessage() == null ? "Ocurrió un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
