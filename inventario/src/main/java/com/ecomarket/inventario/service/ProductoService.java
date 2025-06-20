package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.ProductoRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepo;

    public ProductoService(ProductoRepository productoRepo, ProveedorRepository proveedorRepo) {
        this.productoRepo = productoRepo;
        this.proveedorRepo = proveedorRepo;
    }

    public Producto guardarProducto(Producto producto) {

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a cero.");
        }


        Proveedor proveedorCompleto = proveedorRepo.findById(producto.getProveedor().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));


        Optional<Producto> existente = productoRepo.findByNombreAndProveedorId(
                producto.getNombre(), proveedorCompleto.getId());

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un producto con ese nombre para este proveedor.");
        }

        producto.setProveedor(proveedorCompleto);  // Asignar proveedor completo

        return productoRepo.save(producto);
    }



    public List<Producto> obtenerTodos() {
        return productoRepo.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public void eliminarProducto(Long id) {
        productoRepo.deleteById(id);
    }

    public Producto actualizarProducto(Long id, Producto datosActualizados) {
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a cero.");
        }

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

