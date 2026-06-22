package com.eva2.staem.promociones.controller;

import com.eva2.staem.promociones.dto.PromocionRequestDTO;
import com.eva2.staem.promociones.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearPromocion(@Valid @RequestBody PromocionRequestDTO request) {
        try {
            return ResponseEntity.ok(promocionService.crearPromocion(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<?> obtenerPromocionesPorJuego(@PathVariable Long juegoId) {
        try {
            return ResponseEntity.ok(promocionService.obtenerPromocionesPorJuego(juegoId));
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