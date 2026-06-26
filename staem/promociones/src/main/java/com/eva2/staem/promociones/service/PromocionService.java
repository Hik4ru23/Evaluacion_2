package com.eva2.staem.promociones.service;

import com.eva2.staem.promociones.dto.PromocionRequestDTO;
import com.eva2.staem.promociones.dto.PromocionResponseDTO;
import com.eva2.staem.promociones.model.Promocion;
import com.eva2.staem.promociones.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private com.eva2.staem.promociones.client.CatalogoClient catalogoClient;

    private void validarJuego(Long juegoId) {
        try {
            if (catalogoClient.buscarPorId(juegoId) == null) 
                throw new IllegalArgumentException("Juego no encontrado con ID: " + juegoId);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Juego no encontrado con ID: " + juegoId);
        }
    }

    public PromocionResponseDTO crearPromocion(PromocionRequestDTO request) {
        validarJuego(request.getJuegoId());
        Promocion promocion = Promocion.builder()
                .juegoId(request.getJuegoId())
                .porcentajeDescuento(request.getPorcentajeDescuento())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        Promocion guardada = promocionRepository.save(promocion);
        return mapearAResponse(guardada);
    }

    public List<PromocionResponseDTO> obtenerPromocionesPorJuego(Long juegoId) {
        validarJuego(juegoId);
        List<Promocion> promociones = promocionRepository.findByJuegoId(juegoId);
        return promociones.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    private PromocionResponseDTO mapearAResponse(Promocion promocion) {
        if (promocion == null) return null;
        return PromocionResponseDTO.builder()
                .id(promocion.getId())
                .juegoId(promocion.getJuegoId())
                .porcentajeDescuento(promocion.getPorcentajeDescuento())
                .fechaInicio(promocion.getFechaInicio())
                .fechaFin(promocion.getFechaFin())
                .build();
    }
}
