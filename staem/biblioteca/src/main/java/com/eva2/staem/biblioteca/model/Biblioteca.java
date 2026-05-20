package com.eva2.staem.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "biblioteca")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId;

    @Column(name = "id_juego", nullable = false)
    private Long juegoId;

    @Column(name = "fecha_adquisicion", nullable = false)
    private LocalDateTime fechaAdquisicion;
}
