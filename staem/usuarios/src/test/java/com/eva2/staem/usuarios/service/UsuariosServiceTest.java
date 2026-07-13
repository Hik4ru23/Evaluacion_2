package com.eva2.staem.usuarios.service;

import com.eva2.staem.usuarios.dto.UsuarioRequestDTO;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.usuarios.model.Usuarios;
import com.eva2.staem.usuarios.repository.UsuariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mindrot.jbcrypt.BCrypt;

import com.eva2.staem.exception.BusinessRuleException;
import com.eva2.staem.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - UsuariosService")
class UsuariosServiceTest {

    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private UsuariosService usuariosService;

    private UsuarioRequestDTO requestDTO;
    private Usuarios usuarioGuardado;

    @BeforeEach
    void setUp() {
        requestDTO = UsuarioRequestDTO.builder()
                .nickname("gamer123")
                .nombre("Gonzalo")
                .correo("gonzalo@mail.com")
                .contrasena("pass1234")
                .saldo(0.0)
                .build();

        usuarioGuardado = Usuarios.builder()
                .id(1L)
                .nickname("gamer123")
                .nombre("Gonzalo")
                .correo("gonzalo@mail.com")
                .contrasena(BCrypt.hashpw("pass1234", BCrypt.gensalt()))
                .saldo(0.0)
                .build();
    }


    @Test
    @DisplayName("crearUsuario - exitoso cuando nickname y correo son únicos")
    void crearUsuario_exitoso() {
        when(usuariosRepository.existsByNickname("gamer123")).thenReturn(false);
        when(usuariosRepository.existsByCorreo("gonzalo@mail.com")).thenReturn(false);
        when(usuariosRepository.save(any(Usuarios.class))).thenReturn(usuarioGuardado);

        UsuarioResponseDTO resultado = usuariosService.crearUsuario(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNickname()).isEqualTo("gamer123");
        assertThat(resultado.getCorreo()).isEqualTo("gonzalo@mail.com");
        verify(usuariosRepository, times(1)).save(any(Usuarios.class));
    }

    @Test
    @DisplayName("crearUsuario - lanza excepcion cuando nickname ya existe")
    void crearUsuario_nicknameRepetido_lanzaExcepcion() {
        when(usuariosRepository.existsByNickname("gamer123")).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.crearUsuario(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta en uso");

        verify(usuariosRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearUsuario - lanza excepcion cuando correo ya está registrado")
    void crearUsuario_correoRepetido_lanzaExcepcion() {
        when(usuariosRepository.existsByNickname("gamer123")).thenReturn(false);
        when(usuariosRepository.existsByCorreo("gonzalo@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.crearUsuario(requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta registrado");

        verify(usuariosRepository, never()).save(any());
    }


    @Test
    @DisplayName("listarUsuarios - retorna lista con todos los usuarios")
    void listarUsuarios_retornaLista() {
        Usuarios otro = Usuarios.builder().id(2L).nickname("player2").nombre("Ana")
                .correo("ana@mail.com").contrasena("abc123").saldo(50.0).build();
        when(usuariosRepository.findAll()).thenReturn(List.of(usuarioGuardado, otro));

        List<UsuarioResponseDTO> resultado = usuariosService.listarUsuarios();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNickname()).isEqualTo("gamer123");
        assertThat(resultado.get(1).getNickname()).isEqualTo("player2");
    }

    @Test
    @DisplayName("listarUsuarios - retorna lista vacía si no hay usuarios")
    void listarUsuarios_listaVacia() {
        when(usuariosRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuariosService.listarUsuarios();

        assertThat(resultado).isEmpty();
    }


    @Test
    @DisplayName("buscarPorId - retorna usuario cuando existe")
    void buscarPorId_encontrado() {
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));

        UsuarioResponseDTO resultado = usuariosService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNickname()).isEqualTo("gamer123");
    }

    @Test
    @DisplayName("buscarPorId - lanza excepcion cuando no existe el ID")
    void buscarPorId_noEncontrado_lanzaExcepcion() {
        when(usuariosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    @DisplayName("eliminarUsuario - elimina correctamente cuando el usuario existe")
    void eliminarUsuario_exitoso() {
        when(usuariosRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuariosRepository).deleteById(1L);

        usuariosService.eliminarUsuario(1L);

        verify(usuariosRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarUsuario - lanza excepcion cuando el usuario no existe")
    void eliminarUsuario_noExiste_lanzaExcepcion() {
        when(usuariosRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuariosService.eliminarUsuario(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(usuariosRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("recargarSaldo - suma correctamente el monto al saldo actual")
    void recargarSaldo_exitoso() {
        usuarioGuardado.setSaldo(100.0);
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));
        when(usuariosRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO resultado = usuariosService.recargarSaldo(1L, 50.0);

        assertThat(resultado.getSaldo()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("recargarSaldo - lanza excepcion cuando el monto es cero o negativo")
    void recargarSaldo_montoInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> usuariosService.recargarSaldo(1L, 0.0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("mayor a 0");

        assertThatThrownBy(() -> usuariosService.recargarSaldo(1L, -10.0))
                .isInstanceOf(BusinessRuleException.class);
    }


    @Test
    @DisplayName("descontarSaldo - descuenta correctamente cuando hay saldo suficiente")
    void descontarSaldo_exitoso() {
        usuarioGuardado.setSaldo(200.0);
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));
        when(usuariosRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO resultado = usuariosService.descontarSaldo(1L, 80.0);

        assertThat(resultado.getSaldo()).isEqualTo(120.0);
    }

    @Test
    @DisplayName("descontarSaldo - lanza excepcion cuando el saldo es insuficiente")
    void descontarSaldo_saldoInsuficiente_lanzaExcepcion() {
        usuarioGuardado.setSaldo(30.0);
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));

        assertThatThrownBy(() -> usuariosService.descontarSaldo(1L, 100.0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Saldo insuficiente");
    }


    @Test
    @DisplayName("validarCredenciales - retorna usuario cuando las credenciales son correctas")
    void validarCredenciales_exitoso() {
        when(usuariosRepository.findByCorreo("gonzalo@mail.com"))
                .thenReturn(Optional.of(usuarioGuardado));

        UsuarioResponseDTO resultado = usuariosService.validarCredenciales("gonzalo@mail.com", "pass1234");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isEqualTo("gonzalo@mail.com");
    }

    @Test
    @DisplayName("validarCredenciales - lanza excepcion cuando la contrasena es incorrecta")
    void validarCredenciales_contrasenaMal_lanzaExcepcion() {
        when(usuariosRepository.findByCorreo("gonzalo@mail.com"))
                .thenReturn(Optional.of(usuarioGuardado));

        assertThatThrownBy(() -> usuariosService.validarCredenciales("gonzalo@mail.com", "wrongpass"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("invalidas");
    }

    @Test
    @DisplayName("validarCredenciales - lanza excepcion cuando el correo no existe")
    void validarCredenciales_correoNoExiste_lanzaExcepcion() {
        when(usuariosRepository.findByCorreo("noexiste@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.validarCredenciales("noexiste@mail.com", "pass1234"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("invalidas");
    }

    @Test
    @DisplayName("buscarPorCorreo - exitoso cuando el correo existe")
    void buscarPorCorreo_exitoso() {
        when(usuariosRepository.findByCorreo("gonzalo@mail.com")).thenReturn(Optional.of(usuarioGuardado));
        
        UsuarioResponseDTO resultado = usuariosService.buscarPorCorreo("gonzalo@mail.com");
        
        assertThat(resultado.getCorreo()).isEqualTo("gonzalo@mail.com");
        assertThat(resultado.getNickname()).isEqualTo("gamer123");
    }

    @Test
    @DisplayName("buscarPorCorreo - lanza excepcion cuando el correo no existe")
    void buscarPorCorreo_noEncontrado() {
        when(usuariosRepository.findByCorreo("noexiste@mail.com")).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> usuariosService.buscarPorCorreo("noexiste@mail.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("noexiste@mail.com");
    }

    @Test
    @DisplayName("actualizarUsuario - exitoso")
    void actualizarUsuario_exitoso() {
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));
        when(usuariosRepository.findByCorreo(requestDTO.getCorreo())).thenReturn(Optional.empty());
        when(usuariosRepository.save(any(Usuarios.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO resultado = usuariosService.actualizarUsuario(1L, requestDTO);

        assertThat(resultado.getNickname()).isEqualTo(requestDTO.getNickname());
        verify(usuariosRepository).save(any(Usuarios.class));
    }

    @Test
    @DisplayName("actualizarUsuario - lanza excepcion si correo esta en uso por otro usuario")
    void actualizarUsuario_correoEnUso() {
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));
        
        Usuarios otroUsuario = Usuarios.builder().id(2L).correo(requestDTO.getCorreo()).build();
        when(usuariosRepository.findByCorreo(requestDTO.getCorreo())).thenReturn(Optional.of(otroUsuario));

        assertThatThrownBy(() -> usuariosService.actualizarUsuario(1L, requestDTO))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("uso");
        
        verify(usuariosRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("actualizarUsuario - lanza excepcion si usuario no existe")
    void actualizarUsuario_noExiste() {
        when(usuariosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.actualizarUsuario(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("descontarSaldo - lanza excepcion si monto es invalido")
    void descontarSaldo_montoInvalido() {
        assertThatThrownBy(() -> usuariosService.descontarSaldo(1L, 0.0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("mayor a 0");

        assertThatThrownBy(() -> usuariosService.descontarSaldo(1L, -5.0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("mayor a 0");
    }
}
