package com.eva2.staem.pagos.repository;

import com.eva2.staem.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagosRepository extends JpaRepository<Pago, Long> {
    // Encontrar usuario por id
    List<Pago> findByUsuarioId(Long usuarioId);
}
