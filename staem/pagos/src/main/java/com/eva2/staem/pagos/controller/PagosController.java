package com.eva2.staem.pagos.controller;

import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.dto.PagoResponseDTO;
import com.eva2.staem.pagos.service.PagosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosController {

    private static final Logger log = LoggerFactory.getLogger(PagosController.class);
    private final PagosService pagosService;

    @PostMapping("/comprar")
    public ResponseEntity<PagoResponseDTO> comprarJuegos(@RequestParam String correoUsuario, @Valid @RequestBody CompraRequestDTO request) {
        log.info("POST /api/pagos/comprar - Usuario: {}", correoUsuario);
        return ResponseEntity.ok(pagosService.procesarCompra(correoUsuario, request));
    }
}
