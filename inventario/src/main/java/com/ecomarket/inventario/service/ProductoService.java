package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.ProductoRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
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

    public Producto registrar(Producto producto) {

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a cero.");
        }

        // Cargar proveedor completo desde la base de datos
        Proveedor proveedorCompleto = proveedorRepo.findById(producto.getProveedor().getId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Verificar si ya existe un producto con ese nombre y proveedor
        Optional<Producto> existente = productoRepo.findByNombreAndProveedorId(
                producto.getNombre(), proveedorCompleto.getId());

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un producto con ese nombre para este proveedor.");
        }

        producto.setProveedor(proveedorCompleto);  // Asignar proveedor completo

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

