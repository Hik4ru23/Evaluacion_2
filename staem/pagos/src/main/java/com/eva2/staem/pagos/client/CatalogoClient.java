package com.eva2.staem.pagos.client;

import com.eva2.staem.catalogo.dto.JuegoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalogo", url = "${ms.catalogo.url}/api/catalogo")
public interface CatalogoClient {

    @GetMapping("/{id}")
    JuegoResponseDTO buscarPorId(@PathVariable("id") Long id);

    @PatchMapping("/{id}/stock")
    JuegoResponseDTO descontarStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
}
