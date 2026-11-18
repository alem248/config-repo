package com.tecsup.producto.service;

import com.tecsup.producto.model.Producto;
import com.tecsup.producto.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final CategoriaCircuitService categoriaCircuitService;

    public ProductoService(ProductoRepository repository, CategoriaCircuitService categoriaCircuitService) {
        this.repository = repository;
        this.categoriaCircuitService = categoriaCircuitService;
    }

    public List<Producto> listar() {
        return repository.findAll().stream()
                .peek(producto -> producto.setCategoria(
                        categoriaCircuitService.obtener(producto.getCategoriaId())))
                .toList();
    }

    public Producto obtener(Long id) {
        return repository.findById(id)
                .map(producto -> {
                    producto.setCategoria(categoriaCircuitService.obtener(producto.getCategoriaId()));
                    return producto;
                })
                .orElse(null);
    }
}
