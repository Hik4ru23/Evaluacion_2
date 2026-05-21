package com.eva2.staem.usuarios.repository;

import com.eva2.staem.usuarios.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByCorreo(String correo);

    Optional<Usuarios> findByNickname(String nickname);

    boolean existsByCorreo(String correo);

    boolean existsByNickname(String nickname);
}
