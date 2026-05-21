package com.eva2.staem.pagos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    @JsonIgnore
    private Pago pago;

    @Column(name = "id_juego", nullable = false, insertable = true, updatable = true)
    private Long juegoId;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;
}
