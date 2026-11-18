package com.tecsup.producto.client;

import com.tecsup.producto.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "categoria-service", path = "/api/categorias")
public interface CategoriaClient {

    @GetMapping("/{id}")
    CategoriaDTO obtener(@PathVariable("id") Long id);
}
