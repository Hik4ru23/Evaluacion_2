package com.eva2.staem.pagos.service;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import com.eva2.staem.pagos.client.BibliotecaClient;
import com.eva2.staem.pagos.client.CatalogoClient;
import com.eva2.staem.pagos.client.UsuariosClient;
import com.eva2.staem.pagos.dto.CompraRequestDTO;
import com.eva2.staem.pagos.dto.PagoResponseDTO;
import com.eva2.staem.pagos.exception.BusinessRuleException;
import com.eva2.staem.pagos.exception.InsufficientFundsException;
import com.eva2.staem.pagos.exception.ResourceNotFoundException;
import com.eva2.staem.pagos.exception.TransactionFailedException;
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

        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new BusinessRuleException("El correo de usuario es obligatorio");
        }
        if (request == null || request.getJuegosIds() == null || request.getJuegosIds().isEmpty()) {
            throw new BusinessRuleException("Debe enviar al menos un juego para comprar");
        }

        UsuarioResponseDTO usuario;
        try {
            usuario = usuariosClient.buscarPorCorreo(correoUsuario);
        } catch (Exception ex) {
            log.error("Error buscando usuario por correo: {} - {}", correoUsuario, ex.getMessage());
            throw new ResourceNotFoundException("No se encontró el usuario con correo: " + correoUsuario);
        }
        if (usuario == null || usuario.getId() == null) {
            throw new ResourceNotFoundException("No se encontró el usuario con correo: " + correoUsuario);
        }

        Long usuarioId = usuario.getId();
        double total = 0.0;
        Pago pago = Pago.builder()
                .usuarioId(usuarioId)
                .fechaTransaccion(LocalDateTime.now())
                .build();

        for (Long juegoId : request.getJuegosIds()) {
            if (juegoId == null) {
                throw new BusinessRuleException("Los IDs de juegos no pueden ser nulos");
            }

            JuegoResponseDTO juego;
            try {
                juego = catalogoClient.buscarPorId(juegoId);
            } catch (Exception ex) {
                log.error("Error buscando el juego con id {} - {}", juegoId, ex.getMessage());
                throw new ResourceNotFoundException("El juego con id " + juegoId + " no existe");
            }
            if (juego == null || juego.getId() == null) {
                throw new ResourceNotFoundException("El juego con id " + juegoId + " no existe");
            }
            if (juego.getStock() <= 0) {
                throw new BusinessRuleException("El juego " + juego.getTitulo() + " no tiene stock");
            }

            total += juego.getPrecio();
            DetallePago detalle = DetallePago.builder()
                    .juegoId(juegoId)
                    .subtotal(juego.getPrecio())
                    .build();
            pago.addDetalle(detalle);
        }

        pago.setTotalPagado(total);

        if (usuario.getSaldo() < total) {
            pago.setEstado("Rechazado - Saldo Insuficiente");
            pagosRepository.save(pago);
            throw new InsufficientFundsException("Saldo insuficiente. Saldo actual: " + usuario.getSaldo() + " - Total a pagar: " + total);
        }

        // ==========================================
        // INICIO PATRÓN SAGA
        // ==========================================
        
        // PASO 1: Descontar Saldo
        try {
            usuariosClient.descontarSaldo(usuarioId, total);
            log.info("Paso 1/3 (Saga): Saldo descontado correctamente.");
        } catch (Exception ex) {
            log.error("Error descontando saldo del usuario {} - {}", usuarioId, ex.getMessage());
            throw new TransactionFailedException("No se pudo descontar el saldo para el usuario. Transacción cancelada.");
        }

        // PASO 2: Descontar Stock
        for (int i = 0; i < request.getJuegosIds().size(); i++) {
            Long juegoId = request.getJuegosIds().get(i);
            try {
                catalogoClient.descontarStock(juegoId, 1);
                log.info("Paso 2/3 (Saga): Stock descontado para juego ID {}", juegoId);
            } catch (Exception ex) {
                log.error("Error descontando stock para el juego {} - {}", juegoId, ex.getMessage());
                
                // COMPENSACIÓN: Devolver el saldo y el stock ya descontado
                log.warn("SAGA ROLLBACK: Fallo al actualizar stock. Iniciando compensación...");
                try {
                    for (int j = 0; j < i; j++) {
                        catalogoClient.agregarStock(request.getJuegosIds().get(j), 1);
                    }
                    usuariosClient.recargarSaldo(usuarioId, total);
                    log.info("SAGA ROLLBACK: Compensación exitosa (Saldo y stock devueltos).");
                } catch (Exception rollbackEx) {
                    log.error("SAGA ROLLBACK FALLIDO (INCONSISTENCIA CRITICA): {}", rollbackEx.getMessage());
                }
                
                throw new TransactionFailedException("Fallo al actualizar el catálogo. Compensación ejecutada: Saldo devuelto al usuario.");
            }
        }

        // PASO 3: Registrar en Biblioteca
        BibliotecaRequestDTO bibliotecaReq = BibliotecaRequestDTO.builder()
                .usuarioId(usuarioId)
                .juegosIds(request.getJuegosIds())
                .build();
        try {
            bibliotecaClient.agregarJuegos(bibliotecaReq);
            log.info("Paso 3/3 (Saga): Juegos agregados a la biblioteca.");
        } catch (Exception ex) {
            log.error("Error agregando juegos a la biblioteca del usuario {} - {}", usuarioId, ex.getMessage());
            
            // COMPENSACIÓN: Devolver stock de todos los juegos y el saldo completo
            log.warn("SAGA ROLLBACK: Fallo al registrar en biblioteca. Iniciando compensación...");
            try {
                for (Long idJuego : request.getJuegosIds()) {
                    catalogoClient.agregarStock(idJuego, 1);
                }
                usuariosClient.recargarSaldo(usuarioId, total);
                log.info("SAGA ROLLBACK: Compensación exitosa (Saldo y stock devueltos).");
            } catch (Exception rollbackEx) {
                log.error("SAGA ROLLBACK FALLIDO (INCONSISTENCIA CRITICA): {}", rollbackEx.getMessage());
            }
            
            throw new TransactionFailedException("Fallo al registrar en la biblioteca. Compensación ejecutada: Saldo y stock devueltos.");
        }

        // ==========================================
        // FIN PATRÓN SAGA
        // ==========================================

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
