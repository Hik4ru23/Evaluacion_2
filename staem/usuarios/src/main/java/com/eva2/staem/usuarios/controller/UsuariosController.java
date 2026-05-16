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

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private static final Logger log = LoggerFactory.getLogger(UsuariosController.class);
    private final UsuariosService usuariosService;

    // Crear usuario
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("POST /api/usuarios - Creando usuario: {}", dto.getNickname());
        UsuarioResponseDTO response = usuariosService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        log.info("GET /api/usuarios - Listando usuarios");
        return ResponseEntity.ok(usuariosService.listarUsuarios());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/usuarios/{} - Buscando usuario", id);
        return ResponseEntity.ok(usuariosService.buscarPorId(id));
    }

    // Buscar por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorCorreo(@PathVariable String correo) {
        log.info("GET /api/usuarios/correo/{} - Buscando usuario", correo);
        return ResponseEntity.ok(usuariosService.buscarPorCorreo(correo));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("PUT /api/usuarios/{} - Actualizando usuario", id);
        return ResponseEntity.ok(usuariosService.actualizarUsuario(id, dto));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /api/usuarios/{} - Eliminando usuario", id);
        usuariosService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Recargar
    @PatchMapping("/{id}/saldo")
    public ResponseEntity<UsuarioResponseDTO> recargarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {
        log.info("PATCH /api/usuarios/{}/saldo - Recargando {} de saldo", id, monto);
        return ResponseEntity.ok(usuariosService.recargarSaldo(id, monto));
    }

    // Descontar
    @PatchMapping("/{id}/descontar")
    public ResponseEntity<UsuarioResponseDTO> descontarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {
        log.info("PATCH /api/usuarios/{}/descontar - Descontando {} de saldo", id, monto);
        return ResponseEntity.ok(usuariosService.descontarSaldo(id, monto));
    }

    @PostMapping("/validar")
    public ResponseEntity<UsuarioResponseDTO> validarCredenciales(
            @RequestParam String correo,
            @RequestParam String contrasena) {
        log.info("POST /api/usuarios/validar - Validando credenciales para {}", correo);
        return ResponseEntity.ok(usuariosService.validarCredenciales(correo, contrasena));
    }
}
