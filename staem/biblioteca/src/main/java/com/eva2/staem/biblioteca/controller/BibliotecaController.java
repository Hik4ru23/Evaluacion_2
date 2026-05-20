package com.eva2.staem.biblioteca.controller;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.biblioteca.dto.BibliotecaResponseDTO;
import com.eva2.staem.biblioteca.service.BibliotecaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
public class BibliotecaController {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaController.class);
    private final BibliotecaService bibliotecaService;

    @PostMapping("/agregar")
    public ResponseEntity<List<BibliotecaResponseDTO>> agregarJuegos(@Valid @RequestBody BibliotecaRequestDTO request) {
        log.info("POST /api/biblioteca/agregar - Usuario: {}", request.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bibliotecaService.agregarJuegos(request));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<BibliotecaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}", usuarioId);
        return ResponseEntity.ok(bibliotecaService.listarPorUsuario(usuarioId));
    }
}
