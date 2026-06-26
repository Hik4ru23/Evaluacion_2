package com.eva2.staem.amigos.dto;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nickname;
    private String nombre;
    private String correo;
    private Double saldo;
}
