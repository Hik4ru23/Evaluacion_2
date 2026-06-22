package com.eva2.staem.resenas.service;

import com.eva2.staem.resenas.dto.ResenaRequestDTO;
import com.eva2.staem.resenas.dto.ResenaResponseDTO;
import com.eva2.staem.resenas.model.Resena;
import com.eva2.staem.resenas.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    public ResenaResponseDTO crearResena(ResenaRequestDTO request) {
        Resena resena = Resena.builder()
                .usuarioId(request.getUsuarioId())
                .juegoId(request.getJuegoId())
                .calificacion(request.getCalificacion())
                .comentario(request.getComentario())
                .build();

        Resena guardada = resenaRepository.save(resena);
        return mapearAResponse(guardada);
    }

    public List<ResenaResponseDTO> obtenerResenasPorJuego(Long juegoId) {
        List<Resena> resenas = resenaRepository.findByJuegoId(juegoId);
        return resenas.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    public List<ResenaResponseDTO> obtenerResenasPorUsuario(Long usuarioId) {
        List<Resena> resenas = resenaRepository.findByUsuarioId(usuarioId);
        return resenas.stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    private ResenaResponseDTO mapearAResponse(Resena resena) {
        if (resena == null) return null;
        return ResenaResponseDTO.builder()
                .id(resena.getId())
                .usuarioId(resena.getUsuarioId())
                .juegoId(resena.getJuegoId())
                .calificacion(resena.getCalificacion())
                .comentario(resena.getComentario())
                .fechaResena(resena.getFechaResena())
                .build();
    }
}