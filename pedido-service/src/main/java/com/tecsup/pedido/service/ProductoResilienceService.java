package com.tecsup.pedido.service;

import com.tecsup.pedido.client.ProductoClient;
import com.tecsup.pedido.dto.CategoriaDTO;
import com.tecsup.pedido.dto.ProductoDTO;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductoResilienceService {

    private static final Logger log = LoggerFactory.getLogger(ProductoResilienceService.class);
    private final ProductoClient productoClient;

    public ProductoResilienceService(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    @Bulkhead(name = "productoBulkhead", fallbackMethod = "productoFallback")
    @CircuitBreaker(name = "productoService", fallbackMethod = "productoFallback")
    public ProductoDTO obtener(Long id) {
        log.info("Bulkhead/Circuit Breaker: consultando producto-service id={}", id);
        return productoClient.obtener(id);
    }

    public ProductoDTO productoFallback(Long id, Throwable ex) {
        log.warn("Fallback de producto id={}: {}", id, ex.toString());
        return new ProductoDTO(
                id,
                "Producto temporal no disponible",
                null,
                null,
                null,
                new CategoriaDTO(null, "Categoría no disponible", null));
    }
}
