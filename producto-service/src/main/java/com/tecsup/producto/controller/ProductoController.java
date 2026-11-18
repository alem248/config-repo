package com.tecsup.producto.controller;

import com.tecsup.producto.model.Producto;
import com.tecsup.producto.service.DemoLatency;
import com.tecsup.producto.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final DemoLatency demoLatency;

    public ProductoController(ProductoService productoService, DemoLatency demoLatency) {
        this.productoService = productoService;
        this.demoLatency = demoLatency;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        demoLatency.pause("producto-service");
        Producto producto = productoService.obtener(id);
        return producto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(producto);
    }
}
