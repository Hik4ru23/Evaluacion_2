package com.eva2.staem.carrito.controller;

import com.eva2.staem.carrito.dto.CarritoRequestDTO;
import com.eva2.staem.carrito.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Endpoints para la gestión del carrito de compras de los usuarios")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/agregar")
    @Operation(summary = "Agregar al carrito", description = "Añade un juego al carrito de compras del usuario. Retorna los detalles del carrito agregado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Juego agregado al carrito exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.eva2.staem.carrito.dto.CarritoResponseDTO.class),
                examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"usuarioId\": 123,\n  \"juegoId\": 456,\n  \"fechaAgregado\": \"2023-10-10T12:00:00\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Error de validación o el juego ya está en el carrito",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n  \"error\": \"Validación\",\n  \"message\": \"El juego ya esta en el carrito\"\n}"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Error al conectar a la base de datos\"\n}")))
    })
    public ResponseEntity<?> agregarAlCarrito(
            @Valid 
            @Parameter(description = "Datos para agregar un juego al carrito", required = true)
            @RequestBody CarritoRequestDTO request) {
        try {
            return ResponseEntity.ok(carritoService.agregarAlCarrito(request));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Obtener carrito", description = "Devuelve una lista con los elementos actuales en el carrito del usuario especificado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.eva2.staem.carrito.dto.CarritoResponseDTO.class),
                examples = @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"usuarioId\": 123,\n    \"juegoId\": 456,\n    \"fechaAgregado\": \"2023-10-10T12:00:00\"\n  }\n]"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Error inesperado\"\n}")))
    })
    public ResponseEntity<?> obtenerCarrito(
            @Parameter(description = "ID del usuario para consultar su carrito", example = "123", required = true)
            @PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminar/{usuarioId}/{juegoId}")
    @Operation(summary = "Eliminar del carrito", description = "Elimina un juego específico del carrito de un usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Juego eliminado exitosamente del carrito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Error inesperado\"\n}")))
    })
    public ResponseEntity<?> eliminarDelCarrito(
            @Parameter(description = "ID del usuario propietario del carrito", example = "123", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID del juego a eliminar del carrito", example = "456", required = true)
            @PathVariable Long juegoId) {
        try {
            carritoService.eliminarDelCarrito(usuarioId, juegoId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/vaciar/{usuarioId}")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los juegos del carrito de un usuario, dejándolo vacío.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Error inesperado\"\n}")))
    })
    public ResponseEntity<?> vaciarCarrito(
            @Parameter(description = "ID del usuario para vaciar su carrito", example = "123", required = true)
            @PathVariable Long usuarioId) {
        try {
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.noContent().build();
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
