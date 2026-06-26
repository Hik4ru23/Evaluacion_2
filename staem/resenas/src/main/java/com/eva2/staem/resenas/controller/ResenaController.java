package com.eva2.staem.resenas.controller;

import com.eva2.staem.resenas.dto.ResenaRequestDTO;
import com.eva2.staem.resenas.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.eva2.staem.resenas.dto.ResenaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
@Tag(name = "Reseñas", description = "Endpoints para la gestión de valoraciones y comentarios del catálogo de juegos")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @Operation(summary = "Crear una nueva reseña", description = "Permite a un usuario calificar un juego y dejar un comentario detallado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña creada y guardada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResenaResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 1,\n  \"juegoId\": 10,\n  \"calificacion\": 5,\n  \"comentario\": \"Excelente juego\",\n  \"fechaResena\": \"2023-10-12T10:00:00\"\n}"))),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"Calificación debe ser entre 1 y 5\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @PostMapping("/crear")
    public ResponseEntity<?> crearResena(
            @Parameter(description = "Datos de la reseña a crear", required = true)
            @Valid @RequestBody ResenaRequestDTO request) {
        try {
            return ResponseEntity.ok(resenaService.crearResena(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener reseñas por Juego", description = "Devuelve el listado completo de reseñas asociadas a un ID de juego específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reseñas obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"usuarioId\": 1,\n    \"juegoId\": 10,\n    \"calificacion\": 5,\n    \"comentario\": \"Excelente juego\",\n    \"fechaResena\": \"2023-10-12T10:00:00\"\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<?> obtenerResenasPorJuego(
            @Parameter(description = "ID del juego", required = true, example = "10")
            @PathVariable Long juegoId) {
        try {
            return ResponseEntity.ok(resenaService.obtenerResenasPorJuego(juegoId));
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener reseñas por Usuario", description = "Devuelve el listado completo de reseñas creadadas por un ID de usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reseñas obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"usuarioId\": 1,\n    \"juegoId\": 10,\n    \"calificacion\": 5,\n    \"comentario\": \"Excelente juego\",\n    \"fechaResena\": \"2023-10-12T10:00:00\"\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}")))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerResenasPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(resenaService.obtenerResenasPorUsuario(usuarioId));
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
