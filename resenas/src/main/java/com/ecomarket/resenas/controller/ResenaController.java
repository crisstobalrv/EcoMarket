package com.ecomarket.resenas.controller;

import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.service.ResenaService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Resena resena) {
        try {
            Resena creada = resenaService.crear(resena);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("mensaje", "Reseña creada correctamente");
            response.put("resena", creada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Resena resena) {
        resenaService.editar(id, resena);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña actualizada correctamente."));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada exitosamente."));
    }

}
