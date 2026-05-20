package com.eva2.staem.usuarios.repository;

import com.eva2.staem.usuarios.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

    // Encontrar por correo
    Optional<Usuarios> findByCorreo(String correo);

    // Encontrar por nickname
    Optional<Usuarios> findByNickname(String nickname);

    // Verificar que existe el correo
    boolean existsByCorreo(String correo);

    // Verificar que existe el nickname
    boolean existsByNickname(String nickname);
}
