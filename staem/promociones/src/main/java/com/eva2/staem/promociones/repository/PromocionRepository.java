package com.eva2.staem.promociones.repository;

import com.eva2.staem.promociones.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByJuegoId(Long juegoId);
}
