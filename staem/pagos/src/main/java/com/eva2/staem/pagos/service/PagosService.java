package com.eva2.staem.pagos.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.pagos.client.BibliotecaClient;
import com.eva2.staem.pagos.client.CatalogoClient;
import com.eva2.staem.pagos.client.UsuariosClient;
import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.dto.PagoResponseDTO;
import com.eva2.staem.pagos.model.DetallePago;
import com.eva2.staem.pagos.model.Pago;
import com.eva2.staem.pagos.repository.PagosRepository;
import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagosService {

    private static final Logger log = LoggerFactory.getLogger(PagosService.class);

    private final PagosRepository pagosRepository;
    private final UsuariosClient usuariosClient;
    private final CatalogoClient catalogoClient;
    private final BibliotecaClient bibliotecaClient;

    @Transactional
    public PagoResponseDTO procesarCompra(String correoUsuario, CompraRequestDTO request) {
        log.info("Procesando compra para el usuario con correo: {}", correoUsuario);

        // Validaciones iniciales
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new IllegalArgumentException("El correo de usuario es obligatorio");
        }
        if (request == null || request.getJuegosIds() == null || request.getJuegosIds().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un juego para comprar");
        }

        // Obtener datos del usuario desde el microservicio de usuarios
        UsuarioResponseDTO usuario;
        try {
            usuario = usuariosClient.buscarPorCorreo(correoUsuario);
        } catch (Exception ex) {
            log.error("Error buscando usuario por correo: {}", correoUsuario, ex);
            throw new IllegalArgumentException("No se encontró el usuario con correo: " + correoUsuario);
        }
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("No se encontró el usuario con correo: " + correoUsuario);
        }

        Long usuarioId = usuario.getId();
        double total = 0.0;
        Pago pago = Pago.builder()
                .usuarioId(usuarioId)
                .fechaTransaccion(LocalDateTime.now())
                .build();

        // Procesar cada juego: validar existencia, stock y calcular total
        for (Long juegoId : request.getJuegosIds()) {
            if (juegoId == null) {
                throw new IllegalArgumentException("Los IDs de juegos no pueden ser nulos");
            }

            JuegoResponseDTO juego;
            try {
                juego = catalogoClient.buscarPorId(juegoId);
            } catch (Exception ex) {
                log.error("Error buscando el juego con id {}", juegoId, ex);
                throw new IllegalArgumentException("El juego con id " + juegoId + " no existe");
            }
            if (juego == null || juego.getId() == null) {
                throw new IllegalArgumentException("El juego con id " + juegoId + " no existe");
            }
            if (juego.getStock() <= 0) {
                throw new IllegalArgumentException("El juego " + juego.getTitulo() + " no tiene stock");
            }

            total += juego.getPrecio();
            DetallePago detalle = DetallePago.builder()
                    .juegoId(juegoId)
                    .subtotal(juego.getPrecio())
                    .build();
            pago.addDetalle(detalle);
        }

        pago.setTotalPagado(total);

        // Verificar saldo suficiente antes de procesar
        if (usuario.getSaldo() < total) {
            pago.setEstado("Rechazado - Saldo Insuficiente");
            pagosRepository.save(pago);
            throw new IllegalArgumentException("Saldo insuficiente. Saldo actual: " + usuario.getSaldo() + " - Total: " + total);
        }

        // Descontar saldo del usuario
        try {
            usuariosClient.descontarSaldo(usuarioId, total);
        } catch (Exception ex) {
            log.error("Error descontando saldo del usuario {}", usuarioId, ex);
            throw new IllegalArgumentException("No se pudo descontar el saldo para el usuario");
        }

        // Actualizar stock de cada juego comprado
        for (Long juegoId : request.getJuegosIds()) {
            try {
                catalogoClient.descontarStock(juegoId, 1);
            } catch (Exception ex) {
                log.error("Error descontando stock para el juego {}", juegoId, ex);
                throw new IllegalArgumentException("No se pudo actualizar el stock del juego con id " + juegoId);
            }
        }

        // Agregar juegos a la biblioteca del usuario
        BibliotecaRequestDTO bibliotecaReq = BibliotecaRequestDTO.builder()
                .usuarioId(usuarioId)
                .juegosIds(request.getJuegosIds())
                .build();
        try {
            bibliotecaClient.agregarJuegos(bibliotecaReq);
        } catch (Exception ex) {
            log.error("Error agregando juegos a la biblioteca del usuario {}", usuarioId, ex);
            throw new IllegalArgumentException("No se pudo agregar el/los juego(s) a la biblioteca");
        }

        pago.setEstado("Aprobado");
        Pago guardado = pagosRepository.save(pago);
        log.info("Pago aprobado por total: {}", guardado.getTotalPagado());

        return PagoResponseDTO.builder()
                .id(guardado.getId())
                .usuarioId(guardado.getUsuarioId())
                .totalPagado(guardado.getTotalPagado())
                .fechaTransaccion(guardado.getFechaTransaccion())
                .estado(guardado.getEstado())
                .build();
    }
}
