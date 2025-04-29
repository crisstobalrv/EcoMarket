package com.ecomarket.logistica.controller;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.service.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Envio crearEnvio(@RequestBody Envio envio) {
        return envioService.crearEnvio(envio);
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

    @GetMapping("/pedido/{pedidoId}")
    public List<Envio> obtenerPorPedido(@PathVariable Long pedidoId) {
        return envioService.obtenerPorPedido(pedidoId);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Envio> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> estado) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, estado.get("estado")));
    }
}
