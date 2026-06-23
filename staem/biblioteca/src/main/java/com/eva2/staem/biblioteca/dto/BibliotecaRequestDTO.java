package com.eva2.staem.biblioteca.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaRequestDTO {


    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;


    @NotEmpty(message = "La lista de juegos no puede estar vacia")
    private List<Long> juegosIds;
}
