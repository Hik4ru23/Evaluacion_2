package com.eva2.staem.catalogo.service;

import com.eva2.staem.catalogo.dto.JuegoRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.catalogo.model.Juego;
import com.eva2.staem.catalogo.repository.JuegoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import com.eva2.staem.exception.BusinessRuleException;
import com.eva2.staem.exception.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogoService - Unit Tests")
class CatalogoServiceTest {

    @Mock
    private JuegoRepository juegoRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    private Juego buildJuego(Long id, String titulo, int stock, boolean disponible) {
        return Juego.builder()
                .id(id)
                .titulo(titulo)
                .descripcion("Descripcion de " + titulo)
                .precio(29.99)
                .genero("Accion")
                .desarrollador("Dev Studio")
                .imagenUrl("http://img.test/" + id)
                .stock(stock)
                .disponible(disponible)
                .build();
    }

    private Juego buildJuegoFromRequest(Long id, JuegoRequestDTO req) {
        return Juego.builder()
                .id(id)
                .titulo(req.getTitulo())
                .descripcion(req.getDescripcion())
                .precio(req.getPrecio())
                .genero(req.getGenero())
                .desarrollador(req.getDesarrollador())
                .imagenUrl(req.getImagenUrl())
                .stock(req.getStock())
                .disponible(req.getStock() > 0)
                .build();
    }

    private JuegoRequestDTO buildRequest(String titulo, int stock) {
        return JuegoRequestDTO.builder()
                .titulo(titulo)
                .descripcion("Desc")
                .precio(49.99)
                .genero("RPG")
                .desarrollador("GameDev")
                .imagenUrl("http://img.test/1")
                .stock(stock)
                .build();
    }

    @Test
    @DisplayName("agregarJuego - exitoso: disponible=true cuando stock > 0")
    void agregarJuego_exitoso_disponibleTrue() {
        JuegoRequestDTO request = buildRequest("Half-Life 3", 10);
        Juego guardado = buildJuegoFromRequest(1L, request);

        when(juegoRepository.existsByTituloIgnoreCase("Half-Life 3")).thenReturn(false);
        when(juegoRepository.save(any(Juego.class))).thenReturn(guardado);

        JuegoResponseDTO response = catalogoService.agregarJuego(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitulo()).isEqualTo("Half-Life 3");
        assertThat(response.getStock()).isEqualTo(10);
        assertThat(response.getDisponible()).isTrue();
        verify(juegoRepository).save(any(Juego.class));
    }

    @Test
    @DisplayName("agregarJuego - titulo duplicado lanza IllegalArgumentException")
    void agregarJuego_tituloDuplicado_lanzaExcepcion() {
        JuegoRequestDTO request = buildRequest("Minecraft", 5);
        when(juegoRepository.existsByTituloIgnoreCase("Minecraft")).thenReturn(true);

        assertThatThrownBy(() -> catalogoService.agregarJuego(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Ya existe un juego con el titulo: Minecraft");

        verify(juegoRepository, never()).save(any());
    }

    @Test
    @DisplayName("agregarJuego - stock=0 produce disponible=false")
    void agregarJuego_stockCero_disponibleFalse() {
        JuegoRequestDTO request = buildRequest("Portal 3", 0);
        Juego guardado = buildJuegoFromRequest(2L, request);

        when(juegoRepository.existsByTituloIgnoreCase("Portal 3")).thenReturn(false);
        when(juegoRepository.save(any(Juego.class))).thenReturn(guardado);

        JuegoResponseDTO response = catalogoService.agregarJuego(request);

        assertThat(response.getStock()).isZero();
        assertThat(response.getDisponible()).isFalse();
    }

    @Test
    @DisplayName("listarJuegos - retorna todos los juegos")
    void listarJuegos_retornaLista() {
        List<Juego> juegos = List.of(
                buildJuego(1L, "Juego A", 5, true),
                buildJuego(2L, "Juego B", 0, false)
        );
        when(juegoRepository.findAll()).thenReturn(juegos);

        List<JuegoResponseDTO> result = catalogoService.listarJuegos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitulo()).isEqualTo("Juego A");
        assertThat(result.get(1).getTitulo()).isEqualTo("Juego B");
    }

    @Test
    @DisplayName("listarDisponibles - retorna únicamente los juegos con disponible=true")
    void listarDisponibles_retornaSoloDisponibles() {
        List<Juego> disponibles = List.of(
                buildJuego(1L, "Juego A", 5, true)
        );
        when(juegoRepository.findByDisponibleTrue()).thenReturn(disponibles);

        List<JuegoResponseDTO> result = catalogoService.listarDisponibles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisponible()).isTrue();
        verify(juegoRepository).findByDisponibleTrue();
    }

    @Test
    @DisplayName("buscarPorId - retorna DTO cuando el juego existe")
    void buscarPorId_encontrado() {
        Juego juego = buildJuego(10L, "GTA VI", 8, true);
        when(juegoRepository.findById(10L)).thenReturn(Optional.of(juego));

        JuegoResponseDTO response = catalogoService.buscarPorId(10L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitulo()).isEqualTo("GTA VI");
    }

    @Test
    @DisplayName("buscarPorId - lanza RuntimeException cuando el id no existe")
    void buscarPorId_noEncontrado_lanzaExcepcion() {
        when(juegoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.buscarPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No encontrado: 999");
    }

    @Test
    @DisplayName("buscarPorGenero - retorna lista de juegos por genero")
    void buscarPorGenero_retornaLista() {
        List<Juego> juegos = List.of(buildJuego(1L, "Juego G1", 5, true));
        when(juegoRepository.findByGenero("RPG")).thenReturn(juegos);

        List<JuegoResponseDTO> result = catalogoService.buscarPorGenero("RPG");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Juego G1");
    }

    @Test
    @DisplayName("buscarPorTitulo - retorna lista de juegos por titulo parcial")
    void buscarPorTitulo_retornaLista() {
        List<Juego> juegos = List.of(buildJuego(1L, "Mario Kart", 5, true));
        when(juegoRepository.findByTituloContainingIgnoreCase("Mario")).thenReturn(juegos);

        List<JuegoResponseDTO> result = catalogoService.buscarPorTitulo("Mario");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Mario Kart");
    }

    @Test
    @DisplayName("eliminarJuego - exitoso: llama deleteById cuando el juego existe")
    void eliminarJuego_exitoso() {
        when(juegoRepository.existsById(5L)).thenReturn(true);
        doNothing().when(juegoRepository).deleteById(5L);

        catalogoService.eliminarJuego(5L);

        verify(juegoRepository).deleteById(5L);
    }

    @Test
    @DisplayName("eliminarJuego - lanza RuntimeException cuando el juego no existe")
    void eliminarJuego_noExiste_lanzaExcepcion() {
        when(juegoRepository.existsById(404L)).thenReturn(false);

        assertThatThrownBy(() -> catalogoService.eliminarJuego(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No encontrado: 404");

        verify(juegoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("descontarStock - reduce el stock y actualiza disponibilidad")
    void descontarStock_exitoso() {
        Juego juego = buildJuego(1L, "Cyberpunk 2077", 10, true);
        Juego actualizado = buildJuego(1L, "Cyberpunk 2077", 7, true);

        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juego));
        when(juegoRepository.save(any(Juego.class))).thenReturn(actualizado);

        JuegoResponseDTO response = catalogoService.descontarStock(1L, 3);

        assertThat(response.getStock()).isEqualTo(7);
        assertThat(response.getDisponible()).isTrue();
        verify(juegoRepository).save(any(Juego.class));
    }

    @Test
    @DisplayName("descontarStock - lanza IllegalArgumentException cuando stock es insuficiente")
    void descontarStock_stockInsuficiente_lanzaExcepcion() {
        Juego juego = buildJuego(1L, "Cyberpunk 2077", 2, true);
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juego));

        assertThatThrownBy(() -> catalogoService.descontarStock(1L, 5))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Stock insuficiente para el juego: Cyberpunk 2077");

        verify(juegoRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("descontarStock - lanza IllegalArgumentException cuando cantidad es 0 o menor")
    void descontarStock_cantidadInvalida_lanzaExcepcion() {
        assertThatThrownBy(() -> catalogoService.descontarStock(1L, 0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("La cantidad a descontar debe ser mayor a 0");
    }

    @Test
    @DisplayName("descontarStock - lanza RuntimeException cuando el juego no existe")
    void descontarStock_noEncontrado_lanzaExcepcion() {
        when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.descontarStock(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Juego no encontrado con ID: 99");
    }

    @Test
    @DisplayName("actualizarJuego - actualiza campos y retorna DTO actualizado")
    void actualizarJuego_exitoso() {
        Juego existente = buildJuego(1L, "Titulo Viejo", 5, true);
        JuegoRequestDTO updateRequest = JuegoRequestDTO.builder()
                .titulo("Titulo Nuevo")
                .descripcion("Nueva desc")
                .precio(59.99)
                .genero("Aventura")
                .desarrollador("NuevoDev")
                .imagenUrl("http://img.test/new")
                .stock(15)
                .build();

        Juego actualizado = Juego.builder()
                .id(1L)
                .titulo("Titulo Nuevo")
                .descripcion("Nueva desc")
                .precio(59.99)
                .genero("Aventura")
                .desarrollador("NuevoDev")
                .imagenUrl("http://img.test/new")
                .stock(15)
                .disponible(true)
                .build();

        when(juegoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(juegoRepository.save(any(Juego.class))).thenReturn(actualizado);

        JuegoResponseDTO response = catalogoService.actualizarJuego(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitulo()).isEqualTo("Titulo Nuevo");
        assertThat(response.getPrecio()).isEqualTo(59.99);
        assertThat(response.getStock()).isEqualTo(15);
        assertThat(response.getDisponible()).isTrue();
        verify(juegoRepository).save(any(Juego.class));
    }
    
    @Test
    @DisplayName("actualizarJuego - lanza RuntimeException si no existe")
    void actualizarJuego_noEncontrado_lanzaExcepcion() {
        JuegoRequestDTO updateRequest = buildRequest("Nuevo Titulo", 10);
        when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogoService.actualizarJuego(99L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No encontrado: 99");
    }
}
