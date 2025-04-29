package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepo;

    public ProductoService(ProductoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    public Producto registrar(Producto producto) {
        return productoRepo.save(producto);
    }

    public List<Producto> listarTodos() {
        return productoRepo.findAll();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepo.findById(id);
    }

    public void eliminarPorId(Long id) {
        productoRepo.deleteById(id);
    }

    public Producto actualizarProducto(Long id, Producto datosActualizados) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(datosActualizados.getNombre());
        producto.setDescripcion(datosActualizados.getDescripcion());
        producto.setCategoria(datosActualizados.getCategoria());
        producto.setPrecio(datosActualizados.getPrecio());
        producto.setStock(datosActualizados.getStock());

        return productoRepo.save(producto);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepo.findByCategoria(categoria);
    }

    public void descontarStock(Long id, Integer cantidad) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepo.save(producto);
    }

}

