package com.eva2.staem.pagos.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {

    @NotEmpty(message = "Debe proporcionar al menos un ID de juego para comprar")
    private List<Long> juegosIds;
}
