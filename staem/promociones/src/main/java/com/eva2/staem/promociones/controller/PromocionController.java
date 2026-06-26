package com.eva2.staem.promociones.controller;

import com.eva2.staem.promociones.dto.PromocionRequestDTO;
import com.eva2.staem.promociones.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
@Tag(name = "Promociones", description = "Endpoints para la gestión de descuentos y promociones de juegos en el catálogo")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @Operation(summary = "Crear una nueva promoción", description = "Asigna un porcentaje de descuento a un juego específico dentro de un rango de fechas.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la promoción", required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "PromocionRequest",
                            value = "{\n  \"juegoId\": 1,\n  \"porcentajeDescuento\": 20.0,\n  \"fechaInicio\": \"2026-07-01T00:00:00\",\n  \"fechaFin\": \"2026-07-31T23:59:59\"\n}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción creada y guardada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessResponse",
                                    value = "{\n  \"id\": 1,\n  \"juegoId\": 1,\n  \"porcentajeDescuento\": 20.0,\n  \"fechaInicio\": \"2026-07-01T00:00:00\",\n  \"fechaFin\": \"2026-07-31T23:59:59\"\n}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "ValidationError",
                                    value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"La fecha de fin debe ser posterior a la de inicio\"\n}"
                            ))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "InternalError",
                                    value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}"
                            )))
    })
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

    @Operation(summary = "Obtener promociones por Juego", description = "Devuelve el historial de descuentos aplicados a un ID de juego específico.")
    @Parameter(name = "juegoId", description = "ID del juego a consultar", required = true, example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de promociones obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessListResponse",
                                    value = "[\n  {\n    \"id\": 1,\n    \"juegoId\": 1,\n    \"porcentajeDescuento\": 20.0,\n    \"fechaInicio\": \"2026-07-01T00:00:00\",\n    \"fechaFin\": \"2026-07-31T23:59:59\"\n  }\n]"
                            ))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "InternalError",
                                    value = "{\n  \"error\": \"Error interno\",\n  \"message\": \"Ocurrió un error inesperado\"\n}"
                            )))
    })
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<?> obtenerPromocionesPorJuego(@PathVariable Long juegoId) {
        try {
            return ResponseEntity.ok(promocionService.obtenerPromocionesPorJuego(juegoId));
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
