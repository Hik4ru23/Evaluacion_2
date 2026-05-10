package com.eva2.staem.catalogo.repository;

import com.eva2.staem.catalogo.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

    // Buscar juegos por genero
    List<Juego> findByGenero(String genero);

    // Buscar juegos por desarrollador
    List<Juego> findByDesarrollador(String desarrollador);

    // Listar solo los juegos disponibles
    List<Juego> findByDisponibleTrue();

    // Buscar por titulo
    List<Juego> findByTituloContainingIgnoreCase(String titulo);

    // Verificar si un juego con ese titulo ya existe
    boolean existsByTituloIgnoreCase(String titulo);
}
