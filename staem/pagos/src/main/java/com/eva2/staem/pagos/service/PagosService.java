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

        // Buscar usuario
        UsuarioResponseDTO usuario = usuariosClient.buscarPorCorreo(correoUsuario);
        Long usuarioId = usuario.getId();

        // Calcular total
        double total = 0.0;
        Pago pago = Pago.builder()
                .usuarioId(usuarioId)
                .fechaTransaccion(LocalDateTime.now())
                .build();

        for (Long juegoId : request.getJuegosIds()) {
            JuegoResponseDTO juego = catalogoClient.buscarPorId(juegoId);
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

        // Cobrar
        if (usuario.getSaldo() < total) {
            pago.setEstado("Rechazado - Saldo Insuficiente");
            pagosRepository.save(pago);
            throw new IllegalArgumentException("Saldo insuficiente. Saldo actual: " + usuario.getSaldo() + " - Total: " + total);
        }

        usuariosClient.descontarSaldo(usuarioId, total);

        // Restar stock
        for (Long juegoId : request.getJuegosIds()) {
            catalogoClient.descontarStock(juegoId, 1);
        }

        // Guardar en biblioteca
        BibliotecaRequestDTO bibliotecaReq = BibliotecaRequestDTO.builder()
                .usuarioId(usuarioId)
                .juegosIds(request.getJuegosIds())
                .build();
        bibliotecaClient.agregarJuegos(bibliotecaReq);

        // Guardar pago
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
