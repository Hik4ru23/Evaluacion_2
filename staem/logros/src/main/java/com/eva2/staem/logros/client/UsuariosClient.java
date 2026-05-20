package com.eva2.staem.logros.client;

import com.eva2.staem.usuarios.dto.UsuarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "usuarios", url = "${ms.usuarios.url}/api/usuarios")
public interface UsuariosClient {

    @GetMapping("/{id}")
    UsuarioResponseDTO buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/correo/{correo}")
    UsuarioResponseDTO buscarPorCorreo(@PathVariable("correo") String correo);

    @PatchMapping("/{id}/descontar")
    UsuarioResponseDTO descontarSaldo(@PathVariable("id") Long id, @RequestParam("monto") Double monto);
}
