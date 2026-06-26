package com.eva2.staem.pagos.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.pagos.client.BibliotecaClient;
import com.eva2.staem.pagos.client.CatalogoClient;
import com.eva2.staem.pagos.client.UsuariosClient;
import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.dto.PagoResponseDTO;
import com.eva2.staem.pagos.model.Pago;
import com.eva2.staem.pagos.repository.PagosRepository;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import com.eva2.staem.pagos.exception.InsufficientFundsException;
import com.eva2.staem.pagos.exception.ResourceNotFoundException;
import com.eva2.staem.pagos.exception.TransactionFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagosServiceTest {

    @Mock
    private PagosRepository pagosRepository;

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private BibliotecaClient bibliotecaClient;

    @InjectMocks
    private PagosService pagosService;

    @Test
    void testProcesarCompra_CorreoNulo() {
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(1L)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            pagosService.procesarCompra(null, request)
        );
        assertEquals("El correo de usuario es obligatorio", exception.getMessage());
    }

    @Test
    void testProcesarCompra_RequestNulo() {
        String correo = "test@test.com";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            pagosService.procesarCompra(correo, null)
        );
        assertEquals("Debe enviar al menos un juego para comprar", exception.getMessage());
    }

    @Test
    void testProcesarCompra_SinJuegos() {
        String correo = "test@test.com";
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.emptyList()).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("Debe enviar al menos un juego para comprar", exception.getMessage());
    }

    @Test
    void testProcesarCompra_UsuarioNoEncontrado() {
        String correo = "test@test.com";
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(1L)).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("No se encontró el usuario con correo: test@test.com", exception.getMessage());
    }

    @Test
    void testProcesarCompra_UsuarioError() {
        String correo = "test@test.com";
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(1L)).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenThrow(new RuntimeException("Error"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("No se encontró el usuario con correo: test@test.com", exception.getMessage());
    }

    @Test
    void testProcesarCompra_JuegoIdNulo() {
        String correo = "test@test.com";
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Arrays.asList((Long) null)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("Los IDs de juegos no pueden ser nulos", exception.getMessage());
    }

    @Test
    void testProcesarCompra_JuegoNoEncontrado() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("El juego con id 1 no existe", exception.getMessage());
    }

    @Test
    void testProcesarCompra_JuegoSinStock() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(0).precio(50.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("El juego Juego A no tiene stock", exception.getMessage());
    }

    @Test
    void testProcesarCompra_SaldoInsuficiente() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(10.0).build(); // saldo < precio
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(5).precio(50.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("Saldo insuficiente. Saldo actual: 10.0 - Total a pagar: 50.0", exception.getMessage());
        verify(pagosRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void testProcesarCompra_ErrorDescontarSaldo() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(5).precio(50.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);
        when(usuariosClient.descontarSaldo(1L, 50.0)).thenThrow(new RuntimeException("Error"));

        TransactionFailedException exception = assertThrows(TransactionFailedException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("No se pudo descontar el saldo para el usuario. Transacción cancelada.", exception.getMessage());
    }

    @Test
    void testProcesarCompra_ErrorDescontarStock() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(5).precio(50.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);
        when(usuariosClient.descontarSaldo(1L, 50.0)).thenReturn(usuario);
        when(catalogoClient.descontarStock(1L, 1)).thenThrow(new RuntimeException("Error"));

        TransactionFailedException exception = assertThrows(TransactionFailedException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("Fallo al actualizar el catálogo. Compensación ejecutada: Saldo devuelto al usuario.", exception.getMessage());
        verify(usuariosClient, times(1)).recargarSaldo(1L, 50.0);
    }

    @Test
    void testProcesarCompra_ErrorAgregarBiblioteca() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(5).precio(50.0).build();
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);
        when(usuariosClient.descontarSaldo(1L, 50.0)).thenReturn(usuario);
        when(catalogoClient.descontarStock(1L, 1)).thenReturn(juego);
        when(bibliotecaClient.agregarJuegos(any(BibliotecaRequestDTO.class))).thenThrow(new RuntimeException("Error"));

        TransactionFailedException exception = assertThrows(TransactionFailedException.class, () -> 
            pagosService.procesarCompra(correo, request)
        );
        assertEquals("Fallo al registrar en la biblioteca. Compensación ejecutada: Saldo y stock devueltos.", exception.getMessage());
        verify(usuariosClient, times(1)).recargarSaldo(1L, 50.0);
        verify(catalogoClient, times(1)).agregarStock(1L, 1);
    }

    @Test
    void testProcesarCompra_Exito() {
        String correo = "test@test.com";
        Long juegoId = 1L;
        CompraRequestDTO request = CompraRequestDTO.builder().juegosIds(Collections.singletonList(juegoId)).build();
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder().id(1L).saldo(100.0).build();
        JuegoResponseDTO juego = JuegoResponseDTO.builder().id(juegoId).titulo("Juego A").stock(5).precio(50.0).build();
        
        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(catalogoClient.buscarPorId(juegoId)).thenReturn(juego);
        when(usuariosClient.descontarSaldo(1L, 50.0)).thenReturn(usuario);
        when(catalogoClient.descontarStock(1L, 1)).thenReturn(juego);
        when(bibliotecaClient.agregarJuegos(any(BibliotecaRequestDTO.class))).thenReturn(Collections.emptyList());
        
        Pago pagoGuardado = Pago.builder().id(100L).usuarioId(1L).totalPagado(50.0).estado("Aprobado").build();
        when(pagosRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

        PagoResponseDTO response = pagosService.procesarCompra(correo, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUsuarioId());
        assertEquals(50.0, response.getTotalPagado());
        assertEquals("Aprobado", response.getEstado());

        verify(usuariosClient).descontarSaldo(1L, 50.0);
        verify(catalogoClient).descontarStock(1L, 1);
        verify(bibliotecaClient).agregarJuegos(any(BibliotecaRequestDTO.class));
        verify(pagosRepository).save(any(Pago.class));
    }
}
