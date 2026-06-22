package com.eva2.staem.logros.service;

import com.eva2.staem.logros.dto.LogroRequestDTO;
import com.eva2.staem.logros.dto.LogroResponseDTO;
import com.eva2.staem.logros.model.Logro;
import com.eva2.staem.logros.model.LogroDisponible;
import com.eva2.staem.logros.repository.LogrosRepository;
import com.eva2.staem.logros.repository.LogrosDisponiblesRepository;
import com.eva2.staem.logros.dto.LogroDisponibleResponseDTO;
import com.eva2.staem.logros.dto.LogroDisponibleRequestDTO;
import com.eva2.staem.logros.client.UsuariosClient;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogrosService {

    private static final Logger log = LoggerFactory.getLogger(LogrosService.class);

    private final LogrosRepository logrosRepository;

    private final UsuariosClient usuariosClient;
    
    private final LogrosDisponiblesRepository logrosDisponiblesRepository;

    public LogroResponseDTO desbloquearLogro(String correoUsuario, LogroRequestDTO request) {
        log.info("Intento de desbloquear logro {} para usuario: {}", request.getNombre(), correoUsuario);

        UsuarioResponseDTO usuario = usuariosClient.buscarPorCorreo(correoUsuario);
        Long usuarioId = usuario.getId();
        
        // Si el logro no existe en la tabla de disponibles, lo creamos automáticamente
        Optional<LogroDisponible> optDisponible = logrosDisponiblesRepository.findByJuegoIdAndNombre(request.getJuegoId(), request.getNombre());
        LogroDisponible disponible = optDisponible.orElseGet(() -> {
            LogroDisponible d = LogroDisponible.builder()
                    .juegoId(request.getJuegoId())
                    .nombre(request.getNombre())
                    .descripcion(request.getDescripcion())
                    .puntosXp(request.getPuntosXp())
                    .build();
            return logrosDisponiblesRepository.save(d);
        });

        // Verificar que el usuario no tenga ya este logro desbloqueado
        Optional<Logro> logroExistente = logrosRepository.findByUsuarioIdAndLogroId(usuarioId, disponible.getId());
        if (logroExistente.isPresent()) {
            throw new IllegalArgumentException("El jugador ya ha desbloqueado este logro previamente");
        }

        Logro progreso = Logro.builder()
                .usuarioId(usuarioId)
                .logroId(disponible.getId())
                .desbloqueado(true)
                .build();

        Logro guardado = logrosRepository.save(progreso);
        log.info("Logro desbloqueado con exito. ID: {}", guardado.getId());

        return toDTO(guardado);
    }

    public List<LogroResponseDTO> listarLogrosPorUsuario(Long usuarioId) {
        log.info("Listando logros del usuario ID: {}", usuarioId);
        return logrosRepository.findByUsuarioId(usuarioId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public List<LogroDisponibleResponseDTO> listarLogrosDisponibles() {
        log.info("Listando logros disponibles");
        return logrosDisponiblesRepository.findAll().stream()
                .map(this::toDisponibleDTO)
                .collect(Collectors.toList());
    }

    public LogroDisponibleResponseDTO crearLogroDisponible(LogroDisponibleRequestDTO request) {
        LogroDisponible entity = LogroDisponible.builder()
                .juegoId(request.getJuegoId())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .puntosXp(request.getPuntosXp())
                .build();

        LogroDisponible guardado = logrosDisponiblesRepository.save(entity);
        return toDisponibleDTO(guardado);
    }

    private LogroDisponibleResponseDTO toDisponibleDTO(LogroDisponible entity) {
        return LogroDisponibleResponseDTO.builder()
                .id(entity.getId())
                .juegoId(entity.getJuegoId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .puntosXp(entity.getPuntosXp())
                .build();
    }

    private LogroResponseDTO toDTO(Logro entity) {
        LogroResponseDTO.LogroResponseDTOBuilder b = LogroResponseDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId());
        
        Optional<LogroDisponible> opt = logrosDisponiblesRepository.findById(entity.getLogroId());
        if (opt.isPresent()) {
            LogroDisponible d = opt.get();
            b.juegoId(d.getJuegoId()).nombre(d.getNombre()).puntosXp(d.getPuntosXp());
        }
        return b.build();
    }
}
