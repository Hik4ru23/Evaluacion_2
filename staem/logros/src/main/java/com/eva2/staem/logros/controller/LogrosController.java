package com.eva2.staem.logros.controller;

import com.eva2.staem.logros.dto.LogroRequestDTO;
import com.eva2.staem.logros.service.LogrosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.eva2.staem.logros.dto.LogroDisponibleRequestDTO;
import com.eva2.staem.logros.dto.LogroResponseDTO;
import com.eva2.staem.logros.dto.LogroDisponibleResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
@Tag(name = "Logros", description = "Endpoints para la gestión de logros de los usuarios")
public class LogrosController {

    private static final Logger log = LoggerFactory.getLogger(LogrosController.class);
    private final LogrosService logrosService;

    @PostMapping("/desbloquear")
    @Operation(summary = "Desbloquear logro", description = "Desbloquea un logro para un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Logro desbloqueado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LogroResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"id\": 1, \"usuarioId\": 10, \"juegoId\": 5, \"nombre\": \"Primer Sangre\", \"puntosXp\": 100 }"))),
            @ApiResponse(responseCode = "400", description = "Error de validación o logro ya desbloqueado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Validación\", \"message\": \"El jugador ya ha desbloqueado este logro previamente\" }"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Error interno\", \"message\": \"Ocurrió un error inesperado\" }")))
    })
    public ResponseEntity<?> desbloquearLogro(
            @Parameter(description = "Correo del usuario", example = "usuario@test.com") @RequestParam String correoUsuario, 
            @Valid @RequestBody LogroRequestDTO request) {
        log.info("POST /api/logros/desbloquear - Usuario: {}", correoUsuario);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(logrosService.desbloquearLogro(correoUsuario, request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar por usuario", description = "Devuelve los logros obtenidos por un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de logros obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[ { \"id\": 1, \"usuarioId\": 10, \"juegoId\": 5, \"nombre\": \"Primer Sangre\", \"puntosXp\": 100 } ]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Error interno\", \"message\": \"Ocurrió un error\" }")))
    })
    public ResponseEntity<?> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "10") @PathVariable Long usuarioId) {
        log.info("GET /api/logros/usuario/{}", usuarioId);
        try {
            return ResponseEntity.ok(logrosService.listarLogrosPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar logros disponibles", description = "Devuelve el listado de todos los logros disponibles para desbloquear.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de logros disponibles obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[ { \"id\": 1, \"juegoId\": 5, \"nombre\": \"Primer Sangre\", \"descripcion\": \"Consigue tu primera baja\", \"puntosXp\": 100 } ]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Error interno\", \"message\": \"Ocurrió un error\" }")))
    })
    public ResponseEntity<?> listarDisponibles() {
        log.info("GET /api/logros/disponibles");
        try {
            return ResponseEntity.ok(logrosService.listarLogrosDisponibles());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/disponibles")
    @Operation(summary = "Crear logro disponible", description = "Agrega un nuevo logro a la lista de logros que se pueden desbloquear.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Logro disponible creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LogroDisponibleResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"id\": 1, \"juegoId\": 5, \"nombre\": \"Primer Sangre\", \"descripcion\": \"Consigue tu primera baja\", \"puntosXp\": 100 }"))),
            @ApiResponse(responseCode = "400", description = "Error de validación",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Validación\", \"message\": \"Datos incorrectos\" }"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"error\": \"Error interno\", \"message\": \"Ocurrió un error\" }")))
    })
    public ResponseEntity<?> crearDisponible(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo logro", required = true,
                    content = @Content(examples = @ExampleObject(value = "{ \"juegoId\": 5, \"nombre\": \"Primer Sangre\", \"descripcion\": \"Consigue tu primera baja\", \"puntosXp\": 100 }")))
            @Valid @RequestBody LogroDisponibleRequestDTO request) {
        log.info("POST /api/logros/disponibles - creando logro disponible: {}", request.getNombre());
        try {
            return ResponseEntity.status(201).body(logrosService.crearLogroDisponible(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", status.is4xxClientError() ? "ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n" : "Error interno");
        body.put("message", ex.getMessage() == null ? "OcurriÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
