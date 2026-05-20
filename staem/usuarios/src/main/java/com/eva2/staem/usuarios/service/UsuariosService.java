package com.eva2.staem.usuarios.service;

import com.eva2.staem.usuarios.dto.UsuarioRequestDTO;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.usuarios.model.Usuarios;
import com.eva2.staem.usuarios.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private static final Logger log = LoggerFactory.getLogger(UsuariosService.class);
    private final UsuariosRepository usuariosRepository;
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        log.info("Intentando crear usuario con nickname: {}", dto.getNickname());

        // Validar unicos
        if (usuariosRepository.existsByNickname(dto.getNickname())) {
            log.warn("Nickname ya en uso: {}", dto.getNickname());
            throw new IllegalArgumentException("El nickname '" + dto.getNickname() + "' ya esta en uso");
        }
        if (usuariosRepository.existsByCorreo(dto.getCorreo())) {
            log.warn("Correo ya registrado: {}", dto.getCorreo());
            throw new IllegalArgumentException("El correo '" + dto.getCorreo() + "' ya esta registrado");
        }

        Usuarios usuario = Usuarios.builder()
                .nickname(dto.getNickname())
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .contrasena(dto.getContrasena())
                .saldo(0.0) // Regla de negocio: Saldo inicial en 0
                .build();

        Usuarios guardado = usuariosRepository.save(usuario);
        log.info("Usuario creado con ID: {}", guardado.getId());
        return mapToResponse(guardado);
    }

    // Listar
    public List<UsuarioResponseDTO> listarUsuarios() {
        log.info("Listando todos los usuarios");
        return usuariosRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public UsuarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con ID: {}", id);
                    return new RuntimeException("Usuario no encontrado con ID: " + id);
                });
        return mapToResponse(usuario);
    }

    // Buscar por correo
    public UsuarioResponseDTO buscarPorCorreo(String correo) {
        log.info("Buscando usuario con correo: {}", correo);
        Usuarios usuario = usuariosRepository.findByCorreo(correo)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con correo: {}", correo);
                    return new RuntimeException("Usuario no encontrado con correo: " + correo);
                });
        return mapToResponse(usuario);
    }

    // Actualizar
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Correo unico
        usuariosRepository.findByCorreo(dto.getCorreo())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("El correo ya esta en uso por otro usuario");
                });

        usuario.setNickname(dto.getNickname());
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(dto.getContrasena());
        usuario.setSaldo(dto.getSaldo());

        Usuarios actualizado = usuariosRepository.save(usuario);
        log.info("Usuario actualizado correctamente con ID: {}", id);
        return mapToResponse(actualizado);
    }

    // Eliminar
    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (!usuariosRepository.existsById(id)) {
            log.error("No se encontro usuario con ID: {} para eliminar", id);
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuariosRepository.deleteById(id);
        log.info("Usuario eliminado con ID: {}", id);
    }

    // Recargar
    public UsuarioResponseDTO recargarSaldo(Long id, Double monto) {
        log.info("Recargando saldo de usuario ID: {} con monto: {}", id, monto);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de recarga debe ser mayor a 0");
        }
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setSaldo(usuario.getSaldo() + monto);
        Usuarios actualizado = usuariosRepository.save(usuario);
        log.info("Nuevo saldo del usuario {}: {}", id, actualizado.getSaldo());
        return mapToResponse(actualizado);
    }

    // Descontar
    public UsuarioResponseDTO descontarSaldo(Long id, Double monto) {
        log.info("Descontando saldo de usuario ID: {} con monto: {}", id, monto);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser mayor a 0");
        }
        Usuarios usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (usuario.getSaldo() < monto) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        usuario.setSaldo(usuario.getSaldo() - monto);
        Usuarios actualizado = usuariosRepository.save(usuario);
        log.info("Nuevo saldo del usuario {}: {}", id, actualizado.getSaldo());
        return mapToResponse(actualizado);
    }

    public UsuarioResponseDTO validarCredenciales(String correo, String contrasena) {
        Usuarios usuario = usuariosRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));
        
        if (!contrasena.equals(usuario.getContrasena())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }
        
        return mapToResponse(usuario);
    }

    // Convertir a DTO
    private UsuarioResponseDTO mapToResponse(Usuarios u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .saldo(u.getSaldo())
                .build();
    }
}
