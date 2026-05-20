package com.eva2.staem.logros.service;

import com.eva2.staem.logros.dto.LogroRequestDTO;
import com.eva2.staem.logros.dto.LogroResponseDTO;
import com.eva2.staem.logros.model.Logro;
import com.eva2.staem.logros.repository.LogrosRepository;
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

    public LogroResponseDTO desbloquearLogro(String correoUsuario, LogroRequestDTO request) {
        log.info("Intento de desbloquear logro {} para usuario: {}", request.getNombre(), correoUsuario);

        UsuarioResponseDTO usuario = usuariosClient.buscarPorCorreo(correoUsuario);
        Long usuarioId = usuario.getId();

        // Evitar duplicados
        Optional<Logro> logroExistente = logrosRepository.findByUsuarioIdAndJuegoIdAndNombre(
                usuarioId, request.getJuegoId(), request.getNombre());

        if (logroExistente.isPresent()) {
            throw new IllegalArgumentException("El jugador ya ha desbloqueado este logro previamente");
        }

        Logro nuevoLogro = Logro.builder()
                .usuarioId(usuarioId)
                .juegoId(request.getJuegoId())
                .nombre(request.getNombre())
                .puntosXp(request.getPuntosXp())
                .build();

        Logro guardado = logrosRepository.save(nuevoLogro);
        log.info("Logro desbloqueado con exito. ID: {}", guardado.getId());

        return toDTO(guardado);
    }

    public List<LogroResponseDTO> listarLogrosPorUsuario(Long usuarioId) {
        log.info("Listando logros del usuario ID: {}", usuarioId);
        return logrosRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private LogroResponseDTO toDTO(Logro entity) {
        return LogroResponseDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .juegoId(entity.getJuegoId())
                .nombre(entity.getNombre())
                .puntosXp(entity.getPuntosXp())
                .build();
    }
}
