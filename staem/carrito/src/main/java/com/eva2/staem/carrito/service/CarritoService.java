package com.eva2.staem.carrito.service;

import com.eva2.staem.carrito.dto.CarritoRequestDTO;
import com.eva2.staem.carrito.dto.CarritoResponseDTO;
import com.eva2.staem.carrito.model.Carrito;
import com.eva2.staem.carrito.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    public CarritoResponseDTO agregarAlCarrito(CarritoRequestDTO request) {
        if (carritoRepository.existsByUsuarioIdAndJuegoId(request.getUsuarioId(), request.getJuegoId())) {
            throw new RuntimeException("El juego ya esta en el carrito");
        }

        Carrito carrito = Carrito.builder()
                .usuarioId(request.getUsuarioId())
                .juegoId(request.getJuegoId())
                .build();

        Carrito guardado = carritoRepository.save(carrito);
        return mapearAResponse(guardado);
    }

    public List<CarritoResponseDTO> obtenerCarrito(Long usuarioId) {
        List<Carrito> items = carritoRepository.findByUsuarioId(usuarioId);
        return items.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDelCarrito(Long usuarioId, Long juegoId) {
        carritoRepository.deleteByUsuarioIdAndJuegoId(usuarioId, juegoId);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        carritoRepository.deleteByUsuarioId(usuarioId);
    }

    private CarritoResponseDTO mapearAResponse(Carrito carrito) {
        if (carrito == null) return null;
        return CarritoResponseDTO.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuarioId())
                .juegoId(carrito.getJuegoId())
                .fechaAgregado(carrito.getFechaAgregado())
                .build();
    }
}