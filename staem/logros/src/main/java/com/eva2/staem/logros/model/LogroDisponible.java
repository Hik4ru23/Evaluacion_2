package com.eva2.staem.logros.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "logros_disponibles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogroDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_juego", nullable = false)
    private Long juegoId;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "puntos_xp", nullable = false)
    private Integer puntosXp;
}
