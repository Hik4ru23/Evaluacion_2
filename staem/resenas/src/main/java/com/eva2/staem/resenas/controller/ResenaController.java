package com.eva2.staem.resenas.controller;

import com.eva2.staem.resenas.dto.ResenaRequestDTO;
import com.eva2.staem.resenas.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearResena(@Valid @RequestBody ResenaRequestDTO request) {
        try {
            return ResponseEntity.ok(resenaService.crearResena(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<?> obtenerResenasPorJuego(@PathVariable Long juegoId) {
        try {
            return ResponseEntity.ok(resenaService.obtenerResenasPorJuego(juegoId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerResenasPorUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(resenaService.obtenerResenasPorUsuario(usuarioId));
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