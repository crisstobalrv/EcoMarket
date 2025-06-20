package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.ProductoCreateRequest;
import com.ecomarket.inventario.dto.ProductoUpdateRequest;
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



    public Producto guardarProducto(ProductoCreateRequest request) {

        // Validaciones
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        if (request.getPrecio() == null || request.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a cero.");
        }

        // Buscar proveedor por ID
        Proveedor proveedorCompleto = proveedorRepo.findById(request.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Verificar si ya existe un producto con el mismo nombre y proveedor
        Optional<Producto> existente = productoRepo.findByNombreAndProveedorId(
                request.getNombre(), proveedorCompleto.getId());

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un producto con ese nombre para este proveedor.");
        }

        // Crear y guardar el producto
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .stock(request.getStock())
                .precio(request.getPrecio())
                .proveedor(proveedorCompleto)
                .build();

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

    public Producto actualizarProducto(Long id, ProductoUpdateRequest request) {
        // Buscar el producto original
        Producto producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validaciones sobre los datos entrantes (request)
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }

        if (request.getPrecio() == null || request.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a cero.");
        }

        // Buscar el proveedor indicado en el request
        Proveedor proveedor = proveedorRepo.findById(request.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Actualizar campos
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setStock(request.getStock());
        producto.setPrecio(request.getPrecio());
        producto.setProveedor(proveedor);

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

