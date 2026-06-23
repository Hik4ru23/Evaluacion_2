package com.eva2.staem.carrito.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoRequestDTO {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long juegoId;
}