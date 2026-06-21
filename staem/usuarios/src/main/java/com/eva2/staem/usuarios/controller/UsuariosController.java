package com.eva2.staem.usuarios.controller;

import com.eva2.staem.usuarios.dto.UsuarioRequestDTO;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.usuarios.service.UsuariosService;
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
public class UsuariosController {

    private static final Logger log = LoggerFactory.getLogger(UsuariosController.class);
    private final UsuariosService usuariosService;

    @PostMapping
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
    public ResponseEntity<?> listarUsuarios() {
        log.info("GET /api/usuarios - Listando usuarios");
        try {
            return ResponseEntity.ok(usuariosService.listarUsuarios());
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
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
    public ResponseEntity<?> buscarPorCorreo(@PathVariable String correo) {
        log.info("GET /api/usuarios/correo/{} - Buscando usuario", correo);
        try {
            return ResponseEntity.ok(usuariosService.buscarPorCorreo(correo));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
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
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
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
    public ResponseEntity<?> recargarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {
        log.info("PATCH /api/usuarios/{}/saldo - Recargando {} de saldo", id, monto);
        try {
            return ResponseEntity.ok(usuariosService.recargarSaldo(id, monto));
        } catch (RuntimeException ex) {
            return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}/descontar", method = {RequestMethod.PATCH, RequestMethod.POST})
    public ResponseEntity<?> descontarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {
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
    public ResponseEntity<?> validarCredenciales(
            @RequestParam String correo,
            @RequestParam String contrasena) {
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
        body.put("error", status.is4xxClientError() ? "ValidaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n" : "Error interno");
        body.put("message", ex.getMessage() == null ? "OcurriÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³ un error" : ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
