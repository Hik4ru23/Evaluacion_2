package com.eva2.staem.logros.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId;

    @Column(name = "id_juego", nullable = false)
    private Long juegoId;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "puntos_xp", nullable = false)
    private Integer puntosXp;
}
