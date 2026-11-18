package com.tecsup.cliente.controller;

import com.tecsup.cliente.dto.PedidoResumenDTO;
import com.tecsup.cliente.model.Cliente;
import com.tecsup.cliente.repository.ClienteRepository;
import com.tecsup.cliente.service.DemoLatency;
import com.tecsup.cliente.service.PedidoCircuitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PedidoCircuitService pedidoCircuitService;
    private final DemoLatency demoLatency;

    public ClienteController(
            ClienteRepository clienteRepository,
            PedidoCircuitService pedidoCircuitService,
            DemoLatency demoLatency) {
        this.clienteRepository = clienteRepository;
        this.pedidoCircuitService = pedidoCircuitService;
        this.demoLatency = demoLatency;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        demoLatency.pause("cliente-service");
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pedidos")
    public List<PedidoResumenDTO> listarPedidos(@PathVariable Long id) {
        return pedidoCircuitService.listarPedidos(id);
    }
}
