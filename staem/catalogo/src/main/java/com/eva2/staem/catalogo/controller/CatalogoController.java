package com.eva2.staem.catalogo.controller;

import com.eva2.staem.catalogo.dto.JuegoRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints para la gestión del catálogo de juegos")
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);
    private final CatalogoService catalogoService;

    @PostMapping
    @Operation(summary = "Agregar juego", description = "Agrega un nuevo juego al catálogo. La disponibilidad se calculará basada en el stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego agregado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JuegoResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"titulo\": \"The Legend of Zelda\",\n  \"descripcion\": \"Juego de aventura\",\n  \"precio\": 59.99,\n  \"genero\": \"Aventura\",\n  \"desarrollador\": \"Nintendo\",\n  \"imagenUrl\": \"http://example.com/zelda.jpg\",\n  \"stock\": 10,\n  \"disponible\": true\n}"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o juego duplicado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"Ya existe un juego con el titulo: The Legend of Zelda\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> agregarJuego(
            @Parameter(description = "Datos del juego a agregar", required = true)
            @Valid @RequestBody JuegoRequestDTO dto) {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de juegos recuperado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 1,\n    \"titulo\": \"Minecraft\",\n    \"precio\": 29.99,\n    \"stock\": 50,\n    \"disponible\": true\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juegos disponibles recuperados exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 2,\n    \"titulo\": \"Hades\",\n    \"stock\": 15,\n    \"disponible\": true\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JuegoResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"titulo\": \"Celeste\",\n  \"precio\": 19.99,\n  \"stock\": 5,\n  \"disponible\": true\n}"))),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"No encontrado: 1\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> buscarPorId(
            @Parameter(description = "ID del juego a buscar", required = true, example = "1")
            @PathVariable Long id) {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juegos encontrados para el género especificado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 3,\n    \"titulo\": \"Elden Ring\",\n    \"genero\": \"RPG\",\n    \"stock\": 20,\n    \"disponible\": true\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> buscarPorGenero(
            @Parameter(description = "Género del juego", required = true, example = "RPG")
            @PathVariable String genero) {
        try {
            String decodedGenero = java.net.URLDecoder.decode(genero, java.nio.charset.StandardCharsets.UTF_8);
            log.info("GET /api/catalogo/genero/{}", decodedGenero);
            return ResponseEntity.ok(catalogoService.buscarPorGenero(decodedGenero));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar por título", description = "Obtiene la lista de juegos cuyo título coincida con el parámetro de búsqueda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juegos encontrados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[\n  {\n    \"id\": 4,\n    \"titulo\": \"Super Mario Odyssey\",\n    \"stock\": 30,\n    \"disponible\": true\n  }\n]"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> buscarPorTitulo(
            @Parameter(description = "Parte o título completo a buscar", required = true, example = "Mario")
            @RequestParam String titulo) {
        log.info("GET /api/catalogo/buscar?titulo={}", titulo);
        try {
            return ResponseEntity.ok(catalogoService.buscarPorTitulo(titulo));
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar juego", description = "Actualiza la información de un juego existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JuegoResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"titulo\": \"The Legend of Zelda: Tears of the Kingdom\",\n  \"precio\": 69.99,\n  \"stock\": 50,\n  \"disponible\": true\n}"))),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado o datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"No encontrado: 1\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> actualizarJuego(
            @Parameter(description = "ID del juego a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del juego", required = true)
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"No encontrado: 1\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarJuego(
            @Parameter(description = "ID del juego a eliminar", required = true, example = "1")
            @PathVariable Long id) {
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

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Descontar stock", description = "Descuenta una cantidad específica del stock de un juego.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JuegoResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"titulo\": \"Half-Life\",\n  \"stock\": 5,\n  \"disponible\": true\n}"))),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente, juego no encontrado o cantidad inválida",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"error\": \"Validacion\",\n  \"message\": \"Stock insuficiente para el juego: Half-Life\"\n}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> descontarStock(
            @Parameter(description = "ID del juego al que se descontará stock", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Cantidad a descontar", required = true, example = "2")
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

    @PatchMapping("/{id}/stock/agregar")
    @Operation(summary = "Agregar stock", description = "Aumenta una cantidad específica del stock de un juego. Usado principalmente para compensación transaccional (Saga).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock agregado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JuegoResponseDTO.class),
                            examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"titulo\": \"Half-Life\",\n  \"stock\": 10,\n  \"disponible\": true\n}"))),
            @ApiResponse(responseCode = "400", description = "Juego no encontrado o cantidad inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> agregarStock(
            @Parameter(description = "ID del juego al que se agregará stock", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Cantidad a agregar", required = true, example = "2")
            @RequestParam Integer cantidad) {
        log.info("PATCH /api/catalogo/{}/stock/agregar - Agregando {} de stock", id, cantidad);
        try {
            return ResponseEntity.ok(catalogoService.agregarStock(id, cantidad));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
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
