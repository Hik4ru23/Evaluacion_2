package com.eva2.staem.logros.repository;

import com.eva2.staem.logros.model.LogroDisponible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogrosDisponiblesRepository extends JpaRepository<LogroDisponible, Long> {
    List<LogroDisponible> findByJuegoId(Long juegoId);
    Optional<LogroDisponible> findByJuegoIdAndNombre(Long juegoId, String nombre);
}
