package com.eva2.staem.catalogo.repository;

import com.eva2.staem.catalogo.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    List<Juego> findByGenero(String genero);

    List<Juego> findByDesarrollador(String desarrollador);

    List<Juego> findByDisponibleTrue();

    List<Juego> findByTituloContainingIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCase(String titulo);
}
