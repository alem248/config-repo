package com.tecsup.pedido.client;

import com.tecsup.pedido.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "producto-service", path = "/api/productos")
public interface ProductoClient {

    @GetMapping("/{id}")
    ProductoDTO obtener(@PathVariable("id") Long id);
}
