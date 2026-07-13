package com.eva2.staem.carrito.service;

import com.eva2.staem.carrito.dto.CarritoRequestDTO;
import com.eva2.staem.carrito.dto.CarritoResponseDTO;
import com.eva2.staem.carrito.model.Carrito;
import com.eva2.staem.carrito.repository.CarritoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoService - Unit Tests")
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoService carritoService;

    @Mock
    private com.eva2.staem.carrito.client.UsuariosClient usuariosClient;

    @Mock
    private com.eva2.staem.carrito.client.CatalogoClient catalogoClient;

    @org.junit.jupiter.api.BeforeEach
    void setUpMocks() {
        org.mockito.Mockito.lenient().when(usuariosClient.buscarPorId(org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(new com.eva2.staem.carrito.dto.UsuarioResponseDTO());
        org.mockito.Mockito.lenient().when(catalogoClient.buscarPorId(org.mockito.ArgumentMatchers.anyLong()))
            .thenReturn(new com.eva2.staem.carrito.dto.JuegoResponseDTO());
    }

    @Test
    @DisplayName("agregarAlCarrito - exitoso: guarda y retorna DTO")
    void agregarAlCarrito_exitoso() {
        CarritoRequestDTO request = CarritoRequestDTO.builder()
                .usuarioId(1L)
                .juegoId(10L)
                .build();

        Carrito guardado = Carrito.builder()
                .id(100L)
                .usuarioId(1L)
                .juegoId(10L)
                .fechaAgregado(LocalDateTime.now())
                .build();

        when(carritoRepository.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(guardado);

        CarritoResponseDTO response = carritoService.agregarAlCarrito(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUsuarioId()).isEqualTo(1L);
        assertThat(response.getJuegoId()).isEqualTo(10L);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    @DisplayName("agregarAlCarrito - juego ya en carrito lanza RuntimeException")
    void agregarAlCarrito_juegoYaEnCarrito_lanzaExcepcion() {
        CarritoRequestDTO request = CarritoRequestDTO.builder()
                .usuarioId(1L)
                .juegoId(10L)
                .build();

        when(carritoRepository.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> carritoService.agregarAlCarrito(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El juego ya esta en el carrito");

        verify(carritoRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerCarrito - retorna lista de items del usuario")
    void obtenerCarrito_retornaLista() {
        Long usuarioId = 1L;
        Carrito item1 = Carrito.builder().id(1L).usuarioId(usuarioId).juegoId(10L).fechaAgregado(LocalDateTime.now()).build();
        Carrito item2 = Carrito.builder().id(2L).usuarioId(usuarioId).juegoId(20L).fechaAgregado(LocalDateTime.now()).build();

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(item1, item2));

        List<CarritoResponseDTO> result = carritoService.obtenerCarrito(usuarioId);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getJuegoId()).isEqualTo(10L);
        assertThat(result.get(1).getJuegoId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("obtenerCarrito - retorna lista vacía cuando no hay items")
    void obtenerCarrito_listaVacia() {
        Long usuarioId = 99L;
        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Collections.emptyList());

        List<CarritoResponseDTO> result = carritoService.obtenerCarrito(usuarioId);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("eliminarDelCarrito - delega correctamente al repositorio")
    void eliminarDelCarrito_llamaRepositorio() {
        Long usuarioId = 1L;
        Long juegoId = 10L;
        doNothing().when(carritoRepository).deleteByUsuarioIdAndJuegoId(usuarioId, juegoId);

        carritoService.eliminarDelCarrito(usuarioId, juegoId);

        verify(carritoRepository, times(1)).deleteByUsuarioIdAndJuegoId(usuarioId, juegoId);
    }

    @Test
    @DisplayName("vaciarCarrito - delega correctamente al repositorio")
    void vaciarCarrito_llamaRepositorio() {
        Long usuarioId = 1L;
        doNothing().when(carritoRepository).deleteByUsuarioId(usuarioId);

        carritoService.vaciarCarrito(usuarioId);

        verify(carritoRepository, times(1)).deleteByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("agregarAlCarrito - guarda entidad con usuarioId y juegoId correctos")
    void agregarAlCarrito_guardaConDatosCorrectos() {
        CarritoRequestDTO request = CarritoRequestDTO.builder()
                .usuarioId(5L)
                .juegoId(42L)
                .build();

        Carrito guardado = Carrito.builder()
                .id(200L)
                .usuarioId(5L)
                .juegoId(42L)
                .fechaAgregado(LocalDateTime.now())
                .build();

        when(carritoRepository.existsByUsuarioIdAndJuegoId(5L, 42L)).thenReturn(false);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(guardado);

        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);

        carritoService.agregarAlCarrito(request);

        verify(carritoRepository).save(captor.capture());
        Carrito capturado = captor.getValue();
        assertThat(capturado.getUsuarioId()).isEqualTo(5L);
        assertThat(capturado.getJuegoId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("obtenerCarrito - mapea correctamente todos los campos al DTO")
    void obtenerCarrito_mapeaCamposCorrectamente() {
        Long usuarioId = 3L;
        LocalDateTime fecha = LocalDateTime.of(2025, 6, 1, 12, 0);

        Carrito item = Carrito.builder()
                .id(77L)
                .usuarioId(usuarioId)
                .juegoId(55L)
                .fechaAgregado(fecha)
                .build();

        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(item));

        List<CarritoResponseDTO> result = carritoService.obtenerCarrito(usuarioId);

        assertThat(result).hasSize(1);
        CarritoResponseDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(77L);
        assertThat(dto.getUsuarioId()).isEqualTo(3L);
        assertThat(dto.getJuegoId()).isEqualTo(55L);
        assertThat(dto.getFechaAgregado()).isEqualTo(fecha);
    }
    @Test
    @DisplayName("validarUsuario lanza excepcion si el cliente falla o retorna null")
    void validarUsuario_lanzaExcepcion() {
        when(usuariosClient.buscarPorId(2L)).thenThrow(new RuntimeException("Service down"));
        
        CarritoRequestDTO request = CarritoRequestDTO.builder()
                .usuarioId(2L)
                .juegoId(10L)
                .build();
                
        assertThatThrownBy(() -> carritoService.agregarAlCarrito(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 2");
    }

    @Test
    @DisplayName("validarJuego lanza excepcion si el cliente falla o retorna null")
    void validarJuego_lanzaExcepcion() {
        when(catalogoClient.buscarPorId(200L)).thenReturn(null);
        
        CarritoRequestDTO request = CarritoRequestDTO.builder()
                .usuarioId(1L)
                .juegoId(200L)
                .build();
                
        assertThatThrownBy(() -> carritoService.agregarAlCarrito(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Juego no encontrado con ID: 200");
    }
}
