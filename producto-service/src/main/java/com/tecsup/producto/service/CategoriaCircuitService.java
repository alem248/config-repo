package com.tecsup.producto.service;

import com.tecsup.producto.client.CategoriaClient;
import com.tecsup.producto.dto.CategoriaDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CategoriaCircuitService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaCircuitService.class);
    private final CategoriaClient categoriaClient;

    public CategoriaCircuitService(CategoriaClient categoriaClient) {
        this.categoriaClient = categoriaClient;
    }

    @CircuitBreaker(name = "categoriaService", fallbackMethod = "categoriaFallback")
    public CategoriaDTO obtener(Long id) {
        log.info("Llamando a categoria-service para la categoría {}", id);
        return categoriaClient.obtener(id);
    }

    public CategoriaDTO categoriaFallback(Long id, Throwable ex) {
        log.warn("Fallback de categoría para id {}: {}", id, ex.toString());
        return new CategoriaDTO(id, "Categoría temporal no disponible", null);
    }
}
