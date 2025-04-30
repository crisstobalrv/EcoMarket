package com.ecomarket.resenas.controller;

import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.service.ResenaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping
    public Resena crear(@RequestBody Resena resena) {
        return resenaService.crear(resena);
    }

    @GetMapping
    public List<Resena> listarTodas() {
        return resenaService.listarTodas();
    }

    @GetMapping("/producto/{productoId}")
    public List<Resena> listarPorProducto(@PathVariable Long productoId) {
        return resenaService.listarPorProducto(productoId);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Resena> listarPorCliente(@PathVariable Long clienteId) {
        return resenaService.listarPorCliente(clienteId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> editar(@PathVariable Long id, @RequestBody Resena resena) {
        return ResponseEntity.ok(resenaService.editar(id, resena));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
