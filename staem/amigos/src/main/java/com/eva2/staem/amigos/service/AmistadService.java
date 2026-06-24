package com.eva2.staem.amigos.service;
import com.eva2.staem.amigos.dto.AmistadRequestDTO;
import com.eva2.staem.amigos.dto.AmistadResponseDTO;
import com.eva2.staem.amigos.model.Amistad;
import com.eva2.staem.amigos.repository.AmistadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AmistadService {

    @Autowired
    private AmistadRepository amistadRepository;

    public AmistadResponseDTO enviarSolicitud(AmistadRequestDTO request) {
        if (amistadRepository.existsByUsuarioIdAndAmigoId(request.getUsuarioId(), request.getAmigoId())) {
            throw new RuntimeException("La solicitud de amistad ya existe o ya son amigos.");
        }
        Amistad amistad = Amistad.builder()
                .usuarioId(request.getUsuarioId())
                .amigoId(request.getAmigoId())
                .estado("PENDIENTE")
                .build();
        Amistad guardada = amistadRepository.save(amistad);
        return mapearAResponse(guardada);
    }
    public AmistadResponseDTO responderSolicitud(Long usuarioId, Long amigoId, String accion) {
        Amistad amistad = amistadRepository.findByUsuarioIdAndAmigoId(usuarioId, amigoId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (accion.equalsIgnoreCase("ACEPTAR")) {
            amistad.setEstado("ACEPTADA");
        } else if (accion.equalsIgnoreCase("RECHAZAR")) {
            amistadRepository.delete(amistad);
            return null;
        }
        Amistad actualizada = amistadRepository.save(amistad);
        return mapearAResponse(actualizada);
    }
    public List<AmistadResponseDTO> obtenerAmigos(Long usuarioId) {
        List<Amistad> amistades = amistadRepository.findByUsuarioIdAndEstado(usuarioId, "ACEPTADA");
        return amistades.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }
    private AmistadResponseDTO mapearAResponse(Amistad amistad) {
        if (amistad == null) return null;
        return AmistadResponseDTO.builder()
                .id(amistad.getId())
                .usuarioId(amistad.getUsuarioId())
                .amigoId(amistad.getAmigoId())
                .estado(amistad.getEstado())
                .fechaSolicitud(amistad.getFechaSolicitud())
                .build();
    }
}
