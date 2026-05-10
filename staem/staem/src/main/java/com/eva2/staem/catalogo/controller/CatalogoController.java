package com.eva2.staem.catalogo.controller;

import com.eva2.staem.catalogo.dto.JuegoRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);
    private final CatalogoService catalogoService;

    // Agregar juego
    @PostMapping
    public ResponseEntity<JuegoResponseDTO> agregarJuego(@Valid @RequestBody JuegoRequestDTO dto) {
        log.info("POST /api/catalogo - Agregando juego: {}", dto.getTitulo());
        JuegoResponseDTO response = catalogoService.agregarJuego(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Todos los juegos
    @GetMapping
    public ResponseEntity<List<JuegoResponseDTO>> listarJuegos() {
        log.info("GET /api/catalogo - Listando todos los juegos");
        return ResponseEntity.ok(catalogoService.listarJuegos());
    }

    // Disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<JuegoResponseDTO>> listarDisponibles() {
        log.info("GET /api/catalogo/disponibles");
        return ResponseEntity.ok(catalogoService.listarDisponibles());
    }

    // Por ID
    @GetMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/catalogo/{} - Buscando juego", id);
        return ResponseEntity.ok(catalogoService.buscarPorId(id));
    }

    // Filtrar por genero
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<JuegoResponseDTO>> buscarPorGenero(@PathVariable String genero) {
        log.info("GET /api/catalogo/genero/{}", genero);
        return ResponseEntity.ok(catalogoService.buscarPorGenero(genero));
    }

    // Buscar por titulo
    @GetMapping("/buscar")
    public ResponseEntity<List<JuegoResponseDTO>> buscarPorTitulo(@RequestParam String titulo) {
        log.info("GET /api/catalogo/buscar?titulo={}", titulo);
        return ResponseEntity.ok(catalogoService.buscarPorTitulo(titulo));
    }

    // Actualizar juego
    @PutMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> actualizarJuego(
            @PathVariable Long id,
            @Valid @RequestBody JuegoRequestDTO dto) {
        log.info("PUT /api/catalogo/{} - Actualizando juego", id);
        return ResponseEntity.ok(catalogoService.actualizarJuego(id, dto));
    }

    // Eliminar juego
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarJuego(@PathVariable Long id) {
        log.info("DELETE /api/catalogo/{} - Eliminando juego", id);
        catalogoService.eliminarJuego(id);
        return ResponseEntity.noContent().build();
    }
}
