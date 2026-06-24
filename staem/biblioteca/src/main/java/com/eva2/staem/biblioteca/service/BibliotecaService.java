package com.eva2.staem.biblioteca.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.biblioteca.dto.BibliotecaResponseDTO;
import com.eva2.staem.biblioteca.model.Biblioteca;
import com.eva2.staem.biblioteca.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BibliotecaService {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaService.class);
    private final BibliotecaRepository bibliotecaRepository;

    @Transactional
    public List<BibliotecaResponseDTO> agregarJuegos(BibliotecaRequestDTO request) {
        if (request == null || request.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }
        if (request.getJuegosIds() == null || request.getJuegosIds().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un juego para agregar");
        }

        log.info("Agregando juegos a la biblioteca del usuario: {}", request.getUsuarioId());

        try {
            List<Biblioteca> nuevosJuegos = request.getJuegosIds().stream()
                    .filter(juegoId -> bibliotecaRepository.findByUsuarioIdAndJuegoId(request.getUsuarioId(), juegoId).isEmpty())
                    .map(juegoId -> Biblioteca.builder()
                            .usuarioId(request.getUsuarioId())
                            .juegoId(juegoId)
                            .fechaAdquisicion(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            List<Biblioteca> guardados = bibliotecaRepository.saveAll(nuevosJuegos);
            log.info("Se agregaron {} juegos a la biblioteca", guardados.size());

            return guardados.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error al guardar los juegos en la biblioteca", ex);
            throw new IllegalArgumentException("No se pudo agregar el/los juego(s) a la biblioteca: " + ex.getMessage());
        }
    }

    public List<BibliotecaResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando juegos de la biblioteca para el usuario: {}", usuarioId);
        return bibliotecaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private BibliotecaResponseDTO toDTO(Biblioteca entity) {
        return BibliotecaResponseDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .juegoId(entity.getJuegoId())
                .fechaAdquisicion(entity.getFechaAdquisicion())
                .build();
    }
}
