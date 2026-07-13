package com.eva2.staem.usuarios.controller;

import com.eva2.staem.usuarios.dto.UsuarioRequestDTO;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.usuarios.service.UsuariosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuariosControllerTest {

    @Mock
    private UsuariosService usuariosService;

    @InjectMocks
    private UsuariosController usuariosController;

    private UsuarioRequestDTO requestDTO;
    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = UsuarioRequestDTO.builder()
                .nickname("gamer123")
                .nombre("Gonzalo")
                .correo("gonzalo@mail.com")
                .contrasena("pass1234")
                .saldo(0.0)
                .build();

        responseDTO = UsuarioResponseDTO.builder()
                .id(1L)
                .nickname("gamer123")
                .nombre("Gonzalo")
                .correo("gonzalo@mail.com")
                .saldo(0.0)
                .build();
    }

    @Test
    @DisplayName("POST /api/usuarios - Exito")
    void crearUsuario_exito() {
        when(usuariosService.crearUsuario(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.crearUsuario(requestDTO);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("GET /api/usuarios - Lista usuarios")
    void listarUsuarios() {
        when(usuariosService.listarUsuarios()).thenReturn(List.of(responseDTO));

        ResponseEntity<?> response = usuariosController.listarUsuarios();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} - Exito")
    void buscarPorId_exito() {
        when(usuariosService.buscarPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.buscarPorId(1L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} - Exito")
    void eliminarUsuario() {
        doNothing().when(usuariosService).eliminarUsuario(1L);

        ResponseEntity<?> response = usuariosController.eliminarUsuario(1L);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/saldo - Exito")
    void recargarSaldo() {
        responseDTO.setSaldo(100.0);
        when(usuariosService.recargarSaldo(1L, 100.0)).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.recargarSaldo(1L, 100.0);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("POST /api/usuarios/validar - Exito")
    void validarCredenciales() {
        when(usuariosService.validarCredenciales("gonzalo@mail.com", "pass1234")).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.validarCredenciales("gonzalo@mail.com", "pass1234");
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("GET /api/usuarios/correo/{correo} - Exito")
    void buscarPorCorreo() {
        when(usuariosService.buscarPorCorreo("gonzalo@mail.com")).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.buscarPorCorreo("gonzalo%40mail.com");
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} - Exito")
    void actualizarUsuario() {
        when(usuariosService.actualizarUsuario(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.actualizarUsuario(1L, requestDTO);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/descontar - Exito")
    void descontarSaldo() {
        responseDTO.setSaldo(50.0);
        when(usuariosService.descontarSaldo(1L, 50.0)).thenReturn(responseDTO);

        ResponseEntity<?> response = usuariosController.descontarSaldo(1L, 50.0);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
    }
}
