package com.eva2.staem.soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {
    @NotNull
    private Long usuarioId;

    @NotBlank
    private String asunto;

    @NotBlank
    private String descripcion;
}