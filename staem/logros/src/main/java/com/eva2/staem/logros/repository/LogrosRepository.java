package com.eva2.staem.logros.repository;

import com.eva2.staem.logros.model.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogrosRepository extends JpaRepository<Logro, Long> {
    List<Logro> findByUsuarioId(Long usuarioId);
    Optional<Logro> findByUsuarioIdAndJuegoIdAndNombre(Long usuarioId, Long juegoId, String nombre);
}
