package com.eva2.staem.amigos.controller;

import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.service.AmistadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/amigos")
public class AmistadController {
    @Autowired
    private AmistadService amistadService;

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