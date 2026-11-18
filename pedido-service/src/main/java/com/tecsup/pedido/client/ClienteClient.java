package com.tecsup.pedido.client;

import com.tecsup.pedido.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", path = "/api/clientes")
public interface ClienteClient {

    @GetMapping("/{id}")
    ClienteDTO obtener(@PathVariable("id") Long id);
}
