package com.eva2.staem.logros.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progreso_logros")
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

    @Column(name = "id_logro", nullable = false)
    private Long logroId;

    @Column(name = "desbloqueado", nullable = false)
    private Boolean desbloqueado;
}
