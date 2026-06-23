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

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints para la gestión del catálogo de juegos")
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);
    private final CatalogoService catalogoService;

    @PostMapping
    @Operation(summary = "Agregar juego", description = "Agrega un nuevo juego al catálogo.")
    public ResponseEntity<?> agregarJuego(@Valid @RequestBody JuegoRequestDTO dto) {
        log.info("POST /api/catalogo - Agregando juego: {}", dto.getTitulo());
        try {
            JuegoResponseDTO response = catalogoService.agregarJuego(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @Operation(summary = "Listar juegos", description = "Devuelve el listado completo de juegos en el catálogo.")
    public ResponseEntity<?> listarJuegos() {
        log.info("GET /api/catalogo - Listando todos los juegos");
        try {
            return ResponseEntity.ok(catalogoService.listarJuegos());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar juegos disponibles", description = "Devuelve los juegos que tienen stock mayor a cero.")
    public ResponseEntity<?> listarDisponibles() {
        log.info("GET /api/catalogo/disponibles");
        try {
            return ResponseEntity.ok(catalogoService.listarDisponibles());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar juego por ID", description = "Obtiene los detalles de un juego específico dado su ID.")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/catalogo/{} - Buscando juego", id);
        try {
            return ResponseEntity.ok(catalogoService.buscarPorId(id));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/genero/{genero}")
    @Operation(summary = "Buscar por género", description = "Obtiene la lista de juegos filtrados por género.")
    public ResponseEntity<?> buscarPorGenero(@PathVariable String genero) {
        log.info("GET /api/catalogo/genero/{}", genero);
        try {
            return ResponseEntity.ok(catalogoService.buscarPorGenero(genero));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar por título", description = "Obtiene la lista de juegos cuyo título coincida con el parámetro de búsqueda.")
    public ResponseEntity<?> buscarPorTitulo(@RequestParam String titulo) {
        log.info("GET /api/catalogo/buscar?titulo={}", titulo);
        try {
            return ResponseEntity.ok(catalogoService.buscarPorTitulo(titulo));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar juego", description = "Actualiza la información de un juego existente.")
    public ResponseEntity<?> actualizarJuego(
            @PathVariable Long id,
            @Valid @RequestBody JuegoRequestDTO dto) {
        log.info("PUT /api/catalogo/{} - Actualizando juego", id);
        try {
            return ResponseEntity.ok(catalogoService.actualizarJuego(id, dto));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar juego", description = "Elimina un juego del catálogo.")
    public ResponseEntity<?> eliminarJuego(@PathVariable Long id) {
        log.info("DELETE /api/catalogo/{} - Eliminando juego", id);
        try {
            catalogoService.eliminarJuego(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/stock", method = {RequestMethod.PATCH, RequestMethod.POST})
    @Operation(summary = "Descontar stock", description = "Descuenta una cantidad específica del stock de un juego.")
    public ResponseEntity<?> descontarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        log.info("PATCH/POST /api/catalogo/{}/stock - Descontando {} de stock", id, cantidad);
        try {
            return ResponseEntity.ok(catalogoService.descontarStock(id, cantidad));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", status.is4xxClientError() ? "ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n" : "Error interno");
        body.put("message", ex.getMessage() == null ? "OcurriÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
