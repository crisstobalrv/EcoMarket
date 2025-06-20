package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepo;

    public ProveedorService(ProveedorRepository proveedorRepo) {
        this.proveedorRepo = proveedorRepo;
    }

    public Proveedor guardarProveedor(Proveedor proveedor) {
        // Verificar si ya existe un proveedor con ese RUT
        Optional<Proveedor> existente = proveedorRepo.findByRut(proveedor.getRut());

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe un proveedor registrado con ese RUT.");
        }

        return proveedorRepo.save(proveedor);
    }


    public List<Proveedor> obtenerTodos() {
        return proveedorRepo.findAll();
    }

    public Proveedor obtenerProveedorPorId(Long id) {
        return proveedorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }


    public void eliminarProveedor(Long id) {
        proveedorRepo.deleteById(id);
    }

    public Proveedor actualizarProveedor(Long id, Proveedor datos) {
        Proveedor proveedor = proveedorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setNombre(datos.getNombre());
        proveedor.setCorreo(datos.getCorreo());
        proveedor.setTelefono(datos.getTelefono());
        proveedor.setRut(datos.getRut());

        return proveedorRepo.save(proveedor);
    }
}
