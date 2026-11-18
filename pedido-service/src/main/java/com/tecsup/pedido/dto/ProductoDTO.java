package com.tecsup.pedido.dto;

import java.math.BigDecimal;

public record ProductoDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        Long categoriaId,
        CategoriaDTO categoria) {
}
