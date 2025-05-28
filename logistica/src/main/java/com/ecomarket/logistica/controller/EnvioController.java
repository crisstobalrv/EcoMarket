package com.ecomarket.logistica.controller;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.service.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<?> crearEnvio(@RequestBody Envio envio) {
        Envio creado = envioService.crearEnvio(envio);
        return ResponseEntity
                .status(201)
                .body(Map.of(
                        "mensaje", "Envío creado exitosamente.",
                        "envio", creado
                ));
    }


    @GetMapping
    public List<Envio> listarTodos() {
        return envioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        return envioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/venta/{ventaId}")
    public List<Envio> obtenerPorVenta(@PathVariable Long ventaId) {
        return envioService.obtenerPorVenta(ventaId);
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> estado) {
        try {
            Envio actualizado = envioService.actualizarEstado(id, estado.get("estado"));

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "Estado del envío actualizado correctamente.");
            respuesta.put("nuevoEstado", actualizado.getEstado());

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
