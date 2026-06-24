package com.eva2.staem.logros.service;

import com.eva2.staem.logros.client.UsuariosClient;
import com.eva2.staem.logros.dto.LogroDisponibleRequestDTO;
import com.eva2.staem.logros.dto.LogroDisponibleResponseDTO;
import com.eva2.staem.logros.dto.LogroRequestDTO;
import com.eva2.staem.logros.dto.LogroResponseDTO;
import com.eva2.staem.logros.model.Logro;
import com.eva2.staem.logros.model.LogroDisponible;
import com.eva2.staem.logros.repository.LogrosDisponiblesRepository;
import com.eva2.staem.logros.repository.LogrosRepository;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LogrosServiceTest {

    @Mock
    private LogrosRepository logrosRepository;

    @Mock
    private LogrosDisponiblesRepository logrosDisponiblesRepository;

    @Mock
    private UsuariosClient usuariosClient;

    @InjectMocks
    private LogrosService logrosService;

    @Test
    @DisplayName("desbloquearLogro: exitoso cuando el logroDisponible ya existe en BD")
    void desbloquearLogro_cuandoLogroDisponibleYaExiste_retornaDTO() {
        String correo = "user@test.com";
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder()
                .id(1L).nickname("nick").correo(correo).build();

        LogroDisponible logroDisponible = LogroDisponible.builder()
                .id(10L).juegoId(5L).nombre("Primer logro")
                .descripcion("Desc").puntosXp(100).build();

        LogroRequestDTO request = LogroRequestDTO.builder()
                .juegoId(5L).nombre("Primer logro")
                .descripcion("Desc").puntosXp(100).build();

        Logro logroGuardado = Logro.builder()
                .id(99L).usuarioId(1L).logroId(10L).desbloqueado(true).build();

        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(logrosDisponiblesRepository.findByJuegoIdAndNombre(5L, "Primer logro"))
                .thenReturn(Optional.of(logroDisponible));
        when(logrosRepository.findByUsuarioIdAndLogroId(1L, 10L))
                .thenReturn(Optional.empty());
        when(logrosRepository.save(any(Logro.class))).thenReturn(logroGuardado);
        when(logrosDisponiblesRepository.findById(10L)).thenReturn(Optional.of(logroDisponible));

        LogroResponseDTO result = logrosService.desbloquearLogro(correo, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getUsuarioId()).isEqualTo(1L);
        assertThat(result.getJuegoId()).isEqualTo(5L);
        assertThat(result.getNombre()).isEqualTo("Primer logro");
        assertThat(result.getPuntosXp()).isEqualTo(100);
        verify(logrosRepository).save(any(Logro.class));
    }

    @Test
    @DisplayName("desbloquearLogro: crea nuevo logroDisponible cuando no existe en BD")
    void desbloquearLogro_cuandoLogroDisponibleNoExiste_creaUnoNuevo() {
        String correo = "user2@test.com";
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder()
                .id(2L).nickname("nick2").correo(correo).build();

        LogroRequestDTO request = LogroRequestDTO.builder()
                .juegoId(7L).nombre("Nuevo logro")
                .descripcion("Desc nueva").puntosXp(200).build();

        LogroDisponible logroCreado = LogroDisponible.builder()
                .id(20L).juegoId(7L).nombre("Nuevo logro")
                .descripcion("Desc nueva").puntosXp(200).build();

        Logro logroGuardado = Logro.builder()
                .id(88L).usuarioId(2L).logroId(20L).desbloqueado(true).build();

        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(logrosDisponiblesRepository.findByJuegoIdAndNombre(7L, "Nuevo logro"))
                .thenReturn(Optional.empty());
        when(logrosDisponiblesRepository.save(any(LogroDisponible.class))).thenReturn(logroCreado);
        when(logrosRepository.findByUsuarioIdAndLogroId(2L, 20L))
                .thenReturn(Optional.empty());
        when(logrosRepository.save(any(Logro.class))).thenReturn(logroGuardado);
        when(logrosDisponiblesRepository.findById(20L)).thenReturn(Optional.of(logroCreado));

        LogroResponseDTO result = logrosService.desbloquearLogro(correo, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(88L);
        assertThat(result.getUsuarioId()).isEqualTo(2L);
        assertThat(result.getNombre()).isEqualTo("Nuevo logro");
        assertThat(result.getPuntosXp()).isEqualTo(200);
        verify(logrosDisponiblesRepository).save(any(LogroDisponible.class));
    }

    @Test
    @DisplayName("desbloquearLogro: lanza IllegalArgumentException cuando usuario es null")
    void desbloquearLogro_cuandoUsuarioEsNull_lanzaIllegalArgumentException() {
        String correo = "noexiste@test.com";
        LogroRequestDTO request = LogroRequestDTO.builder()
                .juegoId(1L).nombre("Logro").descripcion("D").puntosXp(50).build();

        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(null);

        assertThatThrownBy(() -> logrosService.desbloquearLogro(correo, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado con correo:");
    }

    @Test
    @DisplayName("desbloquearLogro: lanza IllegalStateException cuando usuario ya tiene el logro")
    void desbloquearLogro_cuandoUsuarioYaTieneLogro_lanzaIllegalStateException() {
        String correo = "user3@test.com";
        UsuarioResponseDTO usuario = UsuarioResponseDTO.builder()
                .id(3L).nickname("nick3").correo(correo).build();

        LogroDisponible logroDisponible = LogroDisponible.builder()
                .id(30L).juegoId(2L).nombre("Logro existente")
                .descripcion("D").puntosXp(75).build();

        LogroRequestDTO request = LogroRequestDTO.builder()
                .juegoId(2L).nombre("Logro existente").descripcion("D").puntosXp(75).build();

        Logro logroYaDesbloqueado = Logro.builder()
                .id(55L).usuarioId(3L).logroId(30L).desbloqueado(true).build();

        when(usuariosClient.buscarPorCorreo(correo)).thenReturn(usuario);
        when(logrosDisponiblesRepository.findByJuegoIdAndNombre(2L, "Logro existente"))
                .thenReturn(Optional.of(logroDisponible));
        when(logrosRepository.findByUsuarioIdAndLogroId(3L, 30L))
                .thenReturn(Optional.of(logroYaDesbloqueado));

        assertThatThrownBy(() -> logrosService.desbloquearLogro(correo, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El jugador ya ha desbloqueado este logro previamente");
    }

    @Test
    @DisplayName("listarLogrosPorUsuario: retorna lista de LogroResponseDTO cuando usuario tiene logros")
    void listarLogrosPorUsuario_conLogros_retornaLista() {
        Long usuarioId = 4L;

        LogroDisponible logroDisponible = LogroDisponible.builder()
                .id(40L).juegoId(3L).nombre("Logro A")
                .descripcion("Desc A").puntosXp(50).build();

        Logro logro1 = Logro.builder()
                .id(1L).usuarioId(usuarioId).logroId(40L).desbloqueado(true).build();
        Logro logro2 = Logro.builder()
                .id(2L).usuarioId(usuarioId).logroId(40L).desbloqueado(true).build();

        when(logrosRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(logro1, logro2));
        when(logrosDisponiblesRepository.findById(40L)).thenReturn(Optional.of(logroDisponible));

        List<LogroResponseDTO> result = logrosService.listarLogrosPorUsuario(usuarioId);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getUsuarioId()).isEqualTo(usuarioId);
    }

    @Test
    @DisplayName("listarLogrosPorUsuario: retorna lista vacía cuando usuario no tiene logros")
    void listarLogrosPorUsuario_sinLogros_retornaListaVacia() {
        Long usuarioId = 5L;
        when(logrosRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());

        List<LogroResponseDTO> result = logrosService.listarLogrosPorUsuario(usuarioId);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("listarLogrosDisponibles: retorna lista de LogroDisponibleResponseDTO")
    void listarLogrosDisponibles_retornaListaCompleta() {
        LogroDisponible l1 = LogroDisponible.builder()
                .id(100L).juegoId(1L).nombre("Logro X").descripcion("DX").puntosXp(30).build();
        LogroDisponible l2 = LogroDisponible.builder()
                .id(101L).juegoId(2L).nombre("Logro Y").descripcion("DY").puntosXp(60).build();

        when(logrosDisponiblesRepository.findAll()).thenReturn(List.of(l1, l2));

        List<LogroDisponibleResponseDTO> result = logrosService.listarLogrosDisponibles();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Logro X");
        assertThat(result.get(1).getNombre()).isEqualTo("Logro Y");
    }

    @Test
    @DisplayName("crearLogroDisponible: guarda entidad y retorna LogroDisponibleResponseDTO correctamente")
    void crearLogroDisponible_guardaYRetornaDTO() {
        LogroDisponibleRequestDTO requestDTO = LogroDisponibleRequestDTO.builder()
                .juegoId(9L).nombre("Super Logro").descripcion("Descripción super").puntosXp(500).build();

        LogroDisponible savedEntity = LogroDisponible.builder()
                .id(200L).juegoId(9L).nombre("Super Logro")
                .descripcion("Descripción super").puntosXp(500).build();

        when(logrosDisponiblesRepository.save(any(LogroDisponible.class))).thenReturn(savedEntity);

        LogroDisponibleResponseDTO result = logrosService.crearLogroDisponible(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200L);
        assertThat(result.getJuegoId()).isEqualTo(9L);
        assertThat(result.getNombre()).isEqualTo("Super Logro");
        assertThat(result.getDescripcion()).isEqualTo("Descripción super");
        assertThat(result.getPuntosXp()).isEqualTo(500);
        verify(logrosDisponiblesRepository).save(any(LogroDisponible.class));
    }
}
