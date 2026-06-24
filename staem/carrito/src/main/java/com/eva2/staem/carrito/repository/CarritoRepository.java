package com.eva2.staem.carrito.repository;

import com.eva2.staem.carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuarioId(Long usuarioId);

    Optional<Carrito> findByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    void deleteByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);
    
    void deleteByUsuarioId(Long usuarioId);
}
