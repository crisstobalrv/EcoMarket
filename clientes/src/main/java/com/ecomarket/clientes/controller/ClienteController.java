package com.ecomarket.clientes.controller;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operaciones CRUD para gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado")
    public ResponseEntity<Cliente> registrar(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.registrar(cliente);
        return ResponseEntity.status(201).body(nuevo);
    }


    @Operation(summary = "Obtener cliente por ID", description = "Devuelve los datos de un cliente")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Listar todos los clientes", description = "Devuelve una lista de todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        List<Cliente> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Actualizar cliente", description = "Modifica los datos de un cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        Cliente actualizado = clienteService.actualizar(id, cliente);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su ID")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
