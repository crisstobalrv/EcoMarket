package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Pedido pedido) {
        Pedido nuevo = pedidoService.registrar(pedido);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "El pedido fue creado exitosamente.");
        response.put("pedido", nuevo);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> estado) {
        Pedido actualizado = pedidoService.actualizarEstado(id, estado.get("estado"));
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarPorId(@PathVariable Long id) {
        pedidoService.eliminarPorId(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "El pedido fue eliminado correctamente.");
        response.put("pedidoId", id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> obtenerPorCliente(@PathVariable Long clienteId) {
        return pedidoService.buscarPorCliente(clienteId);
    }
}
