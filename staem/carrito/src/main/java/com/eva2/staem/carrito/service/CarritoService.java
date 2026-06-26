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

    @Autowired
    private com.eva2.staem.carrito.client.UsuariosClient usuariosClient;

    @Autowired
    private com.eva2.staem.carrito.client.CatalogoClient catalogoClient;

    private void validarUsuario(Long usuarioId) {
        try {
            if (usuariosClient.buscarPorId(usuarioId) == null) 
                throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }
    }

    private void validarJuego(Long juegoId) {
        try {
            if (catalogoClient.buscarPorId(juegoId) == null) 
                throw new IllegalArgumentException("Juego no encontrado con ID: " + juegoId);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + juegoId);
        }
    }

    public CarritoResponseDTO agregarAlCarrito(CarritoRequestDTO request) {
        validarUsuario(request.getUsuarioId());
        validarJuego(request.getJuegoId());
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
        validarUsuario(usuarioId);
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
