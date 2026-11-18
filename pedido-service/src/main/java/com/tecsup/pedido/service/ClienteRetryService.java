package com.tecsup.pedido.service;

import com.tecsup.pedido.client.ClienteClient;
import com.tecsup.pedido.dto.ClienteDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClienteRetryService {

    private static final Logger log = LoggerFactory.getLogger(ClienteRetryService.class);
    private final ClienteClient clienteClient;

    public ClienteRetryService(ClienteClient clienteClient) {
        this.clienteClient = clienteClient;
    }

    @Retry(name = "clienteRetry", fallbackMethod = "clienteFallback")
    public ClienteDTO obtener(Long id) {
        log.info("Retry: consultando cliente-service id={}", id);
        return clienteClient.obtener(id);
    }

    public ClienteDTO clienteFallback(Long id, Throwable ex) {
        log.warn("Fallback de cliente id={}: {}", id, ex.toString());
        return new ClienteDTO(id, "Cliente temporal no disponible", null);
    }
}
