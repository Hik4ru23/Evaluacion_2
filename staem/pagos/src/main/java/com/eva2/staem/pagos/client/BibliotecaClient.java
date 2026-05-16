package com.eva2.staem.pagos.client;

import com.eva2.staem.biblioteca.dto.BibliotecaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "biblioteca", url = "${ms.biblioteca.url}/api/biblioteca")
public interface BibliotecaClient {

    @PostMapping("/agregar")
    List<Object> agregarJuegos(@RequestBody BibliotecaRequestDTO request);
}
