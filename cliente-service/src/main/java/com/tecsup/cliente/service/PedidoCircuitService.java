package com.tecsup.cliente.service;

import com.tecsup.cliente.client.PedidoClient;
import com.tecsup.cliente.dto.PedidoResumenDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoCircuitService {

    private static final Logger log = LoggerFactory.getLogger(PedidoCircuitService.class);
    private final PedidoClient pedidoClient;

    public PedidoCircuitService(PedidoClient pedidoClient) {
        this.pedidoClient = pedidoClient;
    }

    @CircuitBreaker(name = "pedidoService", fallbackMethod = "pedidosFallback")
    public List<PedidoResumenDTO> listarPedidos(Long clienteId) {
        log.info("Consultando pedido-service para cliente {}", clienteId);
        return pedidoClient.listarPorCliente(clienteId);
    }

    public List<PedidoResumenDTO> pedidosFallback(Long clienteId, Throwable ex) {
        log.warn("Circuit Breaker: pedido-service no disponible para cliente {}: {}",
                clienteId, ex.toString());
        return List.of();
    }
}
