package com.eva2.staem.amigos.repository;

import com.eva2.staem.amigos.model.Amistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Long> {
    List<Amistad> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    List<Amistad> findByAmigoIdAndEstado(Long amigoId, String estado);
    boolean existsByUsuarioIdAndAmigoId(Long usuarioId, Long amigoId);
    Optional<Amistad> findByUsuarioIdAndAmigoId(Long usuarioId, Long amigoId);
}
