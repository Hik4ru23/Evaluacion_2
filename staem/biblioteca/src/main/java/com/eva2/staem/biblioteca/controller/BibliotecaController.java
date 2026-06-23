package com.eva2.staem.biblioteca.controller;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.biblioteca.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
@Tag(name = "Biblioteca de Juegos", description = "Endpoints para gestionar la colección personal de videojuegos adquiridos por los usuarios")
public class BibliotecaController {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaController.class);
    private final BibliotecaService bibliotecaService;

    @Operation(summary = "Agregar juegos a la biblioteca", description = "Registra uno o más juegos comprados en el inventario personal del usuario. Ignora los juegos que el usuario ya posee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juegos agregados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o lista de juegos vacía"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarJuegos(@Valid @RequestBody BibliotecaRequestDTO request) {
        log.info("POST /api/biblioteca/agregar - Usuario: {}", request.getUsuarioId());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bibliotecaService.agregarJuegos(request));
        } catch (IllegalArgumentException ex) {
            log.error("Validación fallida al agregar juegos a biblioteca", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Validación");
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception ex) {
            log.error("Error inesperado al agregar juegos a biblioteca", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Error interno");
            body.put("message", "No se pudo agregar el juego a la biblioteca");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @Operation(summary = "Obtener biblioteca por usuario", description = "Devuelve el listado completo de juegos que posee un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}", usuarioId);
        try {
            return ResponseEntity.ok(bibliotecaService.listarPorUsuario(usuarioId));
        } catch (Exception ex) {
            log.error("Error inesperado al listar biblioteca por usuario", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Error interno");
            body.put("message", "No se pudo obtener la biblioteca del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}