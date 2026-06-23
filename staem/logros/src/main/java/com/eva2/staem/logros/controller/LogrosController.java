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

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<?> desbloquearLogro(@RequestParam String correoUsuario, @Valid @RequestBody LogroRequestDTO request) {
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
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/logros/usuario/{}", usuarioId);
        try {
            return ResponseEntity.ok(logrosService.listarLogrosPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar logros disponibles", description = "Devuelve el listado de todos los logros disponibles para desbloquear.")
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
    public ResponseEntity<?> crearDisponible(@Valid @RequestBody LogroDisponibleRequestDTO request) {
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
