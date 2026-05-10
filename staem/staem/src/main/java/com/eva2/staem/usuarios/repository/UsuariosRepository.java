package com.eva2.staem.usuarios.repository;

import com.eva2.staem.usuarios.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    // Buscar por correo
    Optional<Usuarios> findByCorreo(String correo);

    // Buscar por nickname
    Optional<Usuarios> findByNickname(String nickname);

    // Correo ya usado
    boolean existsByCorreo(String correo);

    // Nickname ya usado
    boolean existsByNickname(String nickname);
}
