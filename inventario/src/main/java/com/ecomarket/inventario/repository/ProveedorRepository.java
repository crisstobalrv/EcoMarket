package com.ecomarket.inventario.repository;

import com.ecomarket.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRut(String rut);

}


