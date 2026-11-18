package com.tecsup.cliente.client;

import com.tecsup.cliente.dto.PedidoResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pedido-service", path = "/api/pedidos")
public interface PedidoClient {

    @GetMapping("/cliente/{clienteId}")
    List<PedidoResumenDTO> listarPorCliente(@PathVariable("clienteId") Long clienteId);
}
