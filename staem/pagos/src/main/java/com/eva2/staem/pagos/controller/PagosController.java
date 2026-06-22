package com.eva2.staem.pagos.controller;

import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.service.PagosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosController {

    private static final Logger log = LoggerFactory.getLogger(PagosController.class);
    private final PagosService pagosService;

    @PostMapping("/comprar")
    public ResponseEntity<?> comprarJuegos(@RequestParam String correoUsuario, @Valid @RequestBody CompraRequestDTO request) {
        log.info("POST /api/pagos/comprar - Usuario: {}", correoUsuario);
        try {
            return ResponseEntity.ok(pagosService.procesarCompra(correoUsuario, request));
        } catch (IllegalArgumentException ex) {
            log.error("ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n fallida en compra de juegos", ex);
            Map<String, String> body = new HashMap<>();
            body.put("error", "ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n");
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
