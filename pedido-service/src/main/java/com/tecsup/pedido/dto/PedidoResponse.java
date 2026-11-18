package com.tecsup.pedido.dto;

public record PedidoResponse(
        Long id,
        Long clienteId,
        Long productoId,
        Integer cantidad,
        ClienteDTO cliente,
        ProductoDTO producto) {
}
