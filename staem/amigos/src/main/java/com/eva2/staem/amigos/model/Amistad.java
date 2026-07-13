package com.eva2.staem.amigos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "amistades_comunidad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "amigo_id", nullable = false)
    private Long amigoId;

    @Column(nullable = false, length = 20)
    private String estado; 

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDateTime.now();
    }
}
