package com.eva2.staem.catalogo.service;

import com.eva2.staem.catalogo.dto.JuegoRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.catalogo.model.Juego;
import com.eva2.staem.catalogo.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CatalogoService {

    private static final Logger log = LoggerFactory.getLogger(CatalogoService.class);
    private final JuegoRepository juegoRepository;

    public JuegoResponseDTO agregarJuego(JuegoRequestDTO dto) {
        log.info("agregarJuego: {}", dto.getTitulo());

        // Evitar títulos duplicados en el catálogo
        if (juegoRepository.existsByTituloIgnoreCase(dto.getTitulo())) {
            log.warn("Titulo de juego duplicado: {}", dto.getTitulo());
            throw new IllegalArgumentException("Ya existe un juego con el titulo: " + dto.getTitulo());
        }

        // La disponibilidad se calcula automáticamente según el stock
        Juego juego = Juego.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .genero(dto.getGenero())
                .desarrollador(dto.getDesarrollador())
                .imagenUrl(dto.getImagenUrl())
                .stock(dto.getStock())
                .disponible(dto.getStock() > 0)
                .build();

        Juego guardado = juegoRepository.save(juego);
        log.info("Juego agregado con ID: {}", guardado.getId());
        return toDTO(guardado);
    }

    public List<JuegoResponseDTO> listarJuegos() {
        log.info("listarJuegos");
        return juegoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<JuegoResponseDTO> listarDisponibles() {
        log.info("listarDisponibles");
        return juegoRepository.findByDisponibleTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public JuegoResponseDTO buscarPorId(Long id) {
        log.info("buscarPorId: {}", id);
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("no encontrado: {}", id);
                    return new RuntimeException("No encontrado: " + id);
                });
        return toDTO(juego);
    }

    public List<JuegoResponseDTO> buscarPorGenero(String genero) {
        log.info("buscarPorGenero: {}", genero);
        return juegoRepository.findByGenero(genero)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<JuegoResponseDTO> buscarPorTitulo(String titulo) {
        log.info("buscarPorTitulo: {}", titulo);
        return juegoRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public JuegoResponseDTO actualizarJuego(Long id, JuegoRequestDTO dto) {
        log.info("actualizarJuego: {}", id);
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado: " + id));

        juego.setTitulo(dto.getTitulo());
        juego.setDescripcion(dto.getDescripcion());
        juego.setPrecio(dto.getPrecio());
        juego.setGenero(dto.getGenero());
        juego.setDesarrollador(dto.getDesarrollador());
        juego.setImagenUrl(dto.getImagenUrl());
        juego.setStock(dto.getStock());
        juego.setDisponible(dto.getStock() > 0);

        Juego actualizado = juegoRepository.save(juego);
        log.info("actualizado: {}", id);
        return toDTO(actualizado);
    }

    public void eliminarJuego(Long id) {
        log.info("eliminarJuego: {}", id);
        if (!juegoRepository.existsById(id)) {
            log.warn("no encontrado: {}", id);
            throw new RuntimeException("No encontrado: " + id);
        }
        juegoRepository.deleteById(id);
        log.info("juego {} eliminado", id);
    }

    public JuegoResponseDTO descontarStock(Long id, Integer cantidad) {
        log.info("Descontando {} de stock al juego ID: {}", cantidad, id);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a 0");
        }
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado con ID: " + id));
        if (juego.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el juego: " + juego.getTitulo());
        }
        juego.setStock(juego.getStock() - cantidad);
        juego.setDisponible(juego.getStock() > 0);
        Juego actualizado = juegoRepository.save(juego);
        log.info("Nuevo stock del juego {}: {}", id, actualizado.getStock());
        return toDTO(actualizado);
    }

    private JuegoResponseDTO toDTO(Juego j) {
        return JuegoResponseDTO.builder()
                .id(j.getId())
                .titulo(j.getTitulo())
                .descripcion(j.getDescripcion())
                .precio(j.getPrecio())
                .genero(j.getGenero())
                .desarrollador(j.getDesarrollador())
                .imagenUrl(j.getImagenUrl())
                .stock(j.getStock())
                .disponible(j.getDisponible())
                .build();
    }
}
