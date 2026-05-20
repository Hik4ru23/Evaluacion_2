package com.eva2.staem.logros.controller;

import com.eva2.staem.logros.dto.LogroRequestDTO;
import com.eva2.staem.logros.dto.LogroResponseDTO;
import com.eva2.staem.logros.service.LogrosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
public class LogrosController {

    private static final Logger log = LoggerFactory.getLogger(LogrosController.class);
    private final LogrosService logrosService;

    @PostMapping("/desbloquear")
    public ResponseEntity<LogroResponseDTO> desbloquearLogro(@RequestParam String correoUsuario, @Valid @RequestBody LogroRequestDTO request) {
        log.info("POST /api/logros/desbloquear - Usuario: {}", correoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(logrosService.desbloquearLogro(correoUsuario, request));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LogroResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/logros/usuario/{}", usuarioId);
        return ResponseEntity.ok(logrosService.listarLogrosPorUsuario(usuarioId));
    }
}
