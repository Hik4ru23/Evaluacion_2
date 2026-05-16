package com.eva2.staem.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long usuarioId;
    private Double totalPagado;
    private LocalDateTime fechaTransaccion;
    private String estado;
}
