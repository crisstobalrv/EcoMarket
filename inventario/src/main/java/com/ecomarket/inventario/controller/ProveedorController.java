package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.service.ProveedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Proveedor proveedor) {
        Proveedor guardado = proveedorService.registrar(proveedor);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proveedor registrado correctamente",
                "proveedor", guardado
        ));
    }

    @GetMapping
    public List<Proveedor> listarTodos() {
        return proveedorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return proveedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        Proveedor actualizado = proveedorService.actualizar(id, proveedor);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Proveedor actualizado correctamente",
                "proveedor", actualizado
        ));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Proveedor eliminado correctamente"));
    }


}
