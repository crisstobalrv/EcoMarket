package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
    Optional<Producto> findByNombreAndProveedorId(String nombre, Long proveedorId);
    boolean existsByProveedor_Id(Long proveedorId);

}
