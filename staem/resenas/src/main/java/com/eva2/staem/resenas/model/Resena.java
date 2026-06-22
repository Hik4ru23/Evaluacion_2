package com.eva2.staem.resenas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas_juegos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "juego_id", nullable = false)
    private Long juegoId;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 1000)
    private String comentario;

    @Column(name = "fecha_resena")
    private LocalDateTime fechaResena;

    @PrePersist
    protected void onCreate() {
        this.fechaResena = LocalDateTime.now();
    }
}