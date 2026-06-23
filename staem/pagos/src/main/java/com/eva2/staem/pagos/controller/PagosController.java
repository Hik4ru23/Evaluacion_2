package com.eva2.staem.pagos.controller;

import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.service.PagosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Procesamiento de compras de juegos. Se comunica con los microservicios de Usuarios y Catalogo para validar saldo y stock.")
public class PagosController {

    private static final Logger log = LoggerFactory.getLogger(PagosController.class);
    private final PagosService pagosService;

    @PostMapping("/comprar")
    @Operation(
        summary = "Procesar compra de juegos",
        description = "Procesa el pago de uno o más juegos del catálogo para un usuario. " +
                      "Verifica que el usuario tenga saldo suficiente, que los juegos existan y descuenta el monto total. " +
                      "Se comunica internamente con los microservicios de Usuarios y Catalogo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compra realizada exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"mensaje\": \"Compra realizada correctamente\", \"total\": 59.99, \"juegosComprados\": [\"Elden Ring\", \"Hollow Knight\"]}"))),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente, juego no encontrado o datos inválidos",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validacion\", \"message\": \"Saldo insuficiente para completar la compra\"}"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o fallo en comunicación con otro microservicio",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Error interno\", \"message\": \"No se pudo procesar la compra en este momento\"}")))
    })
    public ResponseEntity<?> comprarJuegos(
            @Parameter(description = "Correo electrónico del usuario que realiza la compra", example = "gonzalo@mail.com")
            @RequestParam String correoUsuario,
            @Valid @RequestBody CompraRequestDTO request) {
        log.info("POST /api/pagos/comprar - Usuario: {}", correoUsuario);
        try {
            return ResponseEntity.ok(pagosService.procesarCompra(correoUsuario, request));
        } catch (IllegalArgumentException ex) {
            log.error("Validacion fallida en compra de juegos", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Validacion");
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception ex) {
            log.error("Error inesperado procesando compra de juegos", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Error interno");
            body.put("message", "No se pudo procesar la compra en este momento");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
