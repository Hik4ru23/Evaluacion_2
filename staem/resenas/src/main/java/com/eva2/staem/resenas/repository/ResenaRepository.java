package com.eva2.staem.resenas.repository;

import com.eva2.staem.resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    List<Resena> findByJuegoId(Long juegoId);
    
    List<Resena> findByUsuarioId(Long usuarioId);
}
