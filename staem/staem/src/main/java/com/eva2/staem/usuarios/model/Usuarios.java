package com.eva2.staem.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nickname es obligatorio")
    @Size(max = 50)
    @Column(unique = true)
    private String nickname;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 50)
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(max = 20)
    private String contrasena;

    @NotNull(message = "El saldo es obligatorio")
    private Double saldo;

}
