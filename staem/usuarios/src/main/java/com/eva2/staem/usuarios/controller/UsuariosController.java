package com.eva2.staem.usuarios.controller;

import com.eva2.staem.usuarios.dto.UsuarioRequestDTO;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.usuarios.service.UsuariosService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión completa de usuarios: registro, consulta, actualización, eliminación y manejo de saldo")
public class UsuariosController {

    private static final Logger log = LoggerFactory.getLogger(UsuariosController.class);
    private final UsuariosService usuariosService;

    @PostMapping
    @Operation(
        summary = "Crear usuario",
        description = "Registra un nuevo usuario en el sistema. El nickname y correo deben ser únicos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\": 1, \"nickname\": \"gamer123\", \"nombre\": \"Gonzalo\", \"correo\": \"gonzalo@mail.com\", \"saldo\": 0.0}"))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nickname/correo ya registrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validacion\", \"message\": \"El nickname ya existe\"}"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Error interno\", \"message\": \"Ocurrio un error inesperado\"}")))
    })
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("POST /api/usuarios - Creando usuario: {}", dto.getNickname());
        try {
            UsuarioResponseDTO response = usuariosService.crearUsuario(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @Operation(
        summary = "Listar usuarios",
        description = "Devuelve una lista completa con todos los usuarios registrados en el sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "[{\"id\": 1, \"nickname\": \"gamer123\", \"nombre\": \"Gonzalo\", \"correo\": \"gonzalo@mail.com\", \"saldo\": 150.0}]"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> listarUsuarios() {
        log.info("GET /api/usuarios - Listando usuarios");
        try {
            return ResponseEntity.ok(usuariosService.listarUsuarios());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar usuario por ID",
        description = "Obtiene la información detallada de un usuario dado su identificador único."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class),
                examples = @ExampleObject(value = "{\"id\": 1, \"nickname\": \"gamer123\", \"nombre\": \"Gonzalo\", \"correo\": \"gonzalo@mail.com\", \"saldo\": 150.0}"))),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado con el ID proporcionado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> buscarPorId(
        @Parameter(description = "ID único del usuario", example = "1") @PathVariable Long id) {
        log.info("GET /api/usuarios/{} - Buscando usuario", id);
        try {
            return ResponseEntity.ok(usuariosService.buscarPorId(id));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/correo/{correo}")
    @Operation(
        summary = "Buscar usuario por correo",
        description = "Busca y retorna un usuario utilizando su dirección de correo electrónico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "400", description = "No existe usuario con ese correo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> buscarPorCorreo(
        @Parameter(description = "Correo electrónico del usuario", example = "gonzalo@mail.com") @PathVariable String correo) {
        try {
            String decodedCorreo = java.net.URLDecoder.decode(correo, java.nio.charset.StandardCharsets.UTF_8);
            log.info("GET /api/usuarios/correo/{} - Buscando usuario", decodedCorreo);
            return ResponseEntity.ok(usuariosService.buscarPorCorreo(decodedCorreo));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario existente. Todos los campos del body son requeridos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"id\": 1, \"nickname\": \"gamer_updated\", \"nombre\": \"Gonzalo\", \"correo\": \"nuevo@mail.com\", \"saldo\": 150.0}"))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> actualizarUsuario(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("PUT /api/usuarios/{} - Actualizando usuario", id);
        try {
            return ResponseEntity.ok(usuariosService.actualizarUsuario(id, dto));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina de forma permanente a un usuario del sistema. Esta acción no se puede deshacer."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (sin contenido)"),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarUsuario(
        @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - Eliminando usuario", id);
        try {
            usuariosService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/saldo")
    @Operation(
        summary = "Recargar saldo",
        description = "Agrega una cantidad de dinero al saldo actual de la cuenta del usuario."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo recargado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"id\": 1, \"nickname\": \"gamer123\", \"saldo\": 250.0}"))),
        @ApiResponse(responseCode = "400", description = "Monto inválido o usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> recargarSaldo(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id,
            @Parameter(description = "Monto a agregar al saldo", example = "100.0") @RequestParam Double monto) {
        log.info("PATCH /api/usuarios/{}/saldo - Recargando {} de saldo", id, monto);
        try {
            return ResponseEntity.ok(usuariosService.recargarSaldo(id, monto));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/descontar")
    @Operation(
        summary = "Descontar saldo",
        description = "Resta una cantidad del saldo del usuario. Usado internamente por otros microservicios al realizar compras."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo descontado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente o usuario no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validacion\", \"message\": \"Saldo insuficiente\"}"))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> descontarSaldo(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id,
            @Parameter(description = "Monto a descontar del saldo", example = "50.0") @RequestParam Double monto) {
        log.info("PATCH/POST /api/usuarios/{}/descontar - Descontando {} de saldo", id, monto);
        try {
            return ResponseEntity.ok(usuariosService.descontarSaldo(id, monto));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validar")
    @Operation(
        summary = "Validar credenciales",
        description = "Verifica si el correo y contraseña proporcionados son correctos. Usado por el proceso de login."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credenciales válidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "true"))),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o usuario no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> validarCredenciales(
            @Parameter(description = "Correo del usuario", example = "gonzalo@mail.com") @RequestParam String correo,
            @Parameter(description = "Contraseña del usuario", example = "miPass123") @RequestParam String contrasena) {
        log.info("POST /api/usuarios/validar - Validando credenciales para {}", correo);
        try {
            return ResponseEntity.ok(usuariosService.validarCredenciales(correo, contrasena));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", status.is4xxClientError() ? "Validacion" : "Error interno");
        body.put("message", ex.getMessage() == null ? "Ocurrio un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
