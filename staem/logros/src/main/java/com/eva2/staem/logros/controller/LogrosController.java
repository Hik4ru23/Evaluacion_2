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


@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
public class LogrosController {

    private static final Logger log = LoggerFactory.getLogger(LogrosController.class);
    private final LogrosService logrosService;

    @PostMapping("/desbloquear")
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
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/logros/usuario/{}", usuarioId);
        try {
            return ResponseEntity.ok(logrosService.listarLogrosPorUsuario(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> listarDisponibles() {
        log.info("GET /api/logros/disponibles");
        try {
            return ResponseEntity.ok(logrosService.listarLogrosDisponibles());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/disponibles")
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
        body.put("error", status.is4xxClientError() ? "Validación" : "Error interno");
        body.put("message", ex.getMessage() == null ? "Ocurrió un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
