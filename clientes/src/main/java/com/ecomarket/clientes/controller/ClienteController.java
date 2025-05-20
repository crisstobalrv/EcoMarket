package com.ecomarket.clientes.controller;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Cliente cliente) {
            Cliente nuevo = clienteService.registrar(cliente);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "Cliente registrado exitosamente.");
            respuesta.put("cliente", cliente);
            return ResponseEntity.ok(respuesta);

    }



    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente actualizado = clienteService.actualizar(id, cliente);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Cliente actualizado correctamente.",
                    "cliente", actualizado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            clienteService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

}
