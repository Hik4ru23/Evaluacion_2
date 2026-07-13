package com.eva2.staem.amigos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmistadRequestDTO {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long amigoId;
}
