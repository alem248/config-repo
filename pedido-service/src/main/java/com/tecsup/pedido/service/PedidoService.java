package com.tecsup.pedido.service;

import com.tecsup.pedido.dto.PedidoResponse;
import com.tecsup.pedido.dto.PedidoResumenDTO;
import com.tecsup.pedido.model.Pedido;
import com.tecsup.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ClienteRetryService clienteRetryService;
    private final ProductoResilienceService productoResilienceService;

    public PedidoService(
            PedidoRepository repository,
            ClienteRetryService clienteRetryService,
            ProductoResilienceService productoResilienceService) {
        this.repository = repository;
        this.clienteRetryService = clienteRetryService;
        this.productoResilienceService = productoResilienceService;
    }

    public List<PedidoResponse> listar() {
        return repository.findAll().stream()
                .map(this::enriquecer)
                .toList();
    }

    public Optional<PedidoResponse> obtener(Long id) {
        return repository.findById(id).map(this::enriquecer);
    }

    public List<PedidoResumenDTO> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(this::toResumen)
                .toList();
    }

    private PedidoResumenDTO toResumen(Pedido pedido) {
        return new PedidoResumenDTO(
                pedido.getId(), pedido.getClienteId(), pedido.getProductoId(), pedido.getCantidad());
    }

    private PedidoResponse enriquecer(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getProductoId(),
                pedido.getCantidad(),
                clienteRetryService.obtener(pedido.getClienteId()),
                productoResilienceService.obtener(pedido.getProductoId()));
    }
}
