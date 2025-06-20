package com.ecomarket.clientes.controller;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
    public ResponseEntity<EntityModel<Cliente>> registrar(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.registrar(cliente);

        EntityModel<Cliente> model = EntityModel.of(nuevo);
        model.add(linkTo(methodOn(ClienteController.class).obtenerPorId(nuevo.getId())).withSelfRel());
        model.add(linkTo(methodOn(ClienteController.class).listar()).withRel("todos"));
        model.add(linkTo(methodOn(ClienteController.class).actualizar(nuevo.getId(), nuevo)).withRel("actualizar"));
        model.add(linkTo(methodOn(ClienteController.class).eliminar(nuevo.getId())).withRel("eliminar"));

        return ResponseEntity.status(201).body(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Devuelve los datos de un cliente")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<EntityModel<Cliente>> obtenerPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);

        EntityModel<Cliente> model = EntityModel.of(cliente);
        model.add(linkTo(methodOn(ClienteController.class).obtenerPorId(id)).withSelfRel());
        model.add(linkTo(methodOn(ClienteController.class).listar()).withRel("todos"));
        model.add(linkTo(methodOn(ClienteController.class).actualizar(id, cliente)).withRel("actualizar"));
        model.add(linkTo(methodOn(ClienteController.class).eliminar(id)).withRel("eliminar"));

        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Listar todos los clientes", description = "Devuelve una lista de todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente")
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> listar() {
        List<Cliente> clientes = clienteService.obtenerTodos();

        List<EntityModel<Cliente>> modelos = clientes.stream().map(c -> {
            EntityModel<Cliente> model = EntityModel.of(c);
            model.add(linkTo(methodOn(ClienteController.class).obtenerPorId(c.getId())).withSelfRel());
            model.add(linkTo(methodOn(ClienteController.class).actualizar(c.getId(), c)).withRel("actualizar"));
            model.add(linkTo(methodOn(ClienteController.class).eliminar(c.getId())).withRel("eliminar"));
            return model;
        }).collect(Collectors.toList());

        CollectionModel<EntityModel<Cliente>> collectionModel = CollectionModel.of(modelos);
        collectionModel.add(linkTo(methodOn(ClienteController.class).listar()).withSelfRel());
        collectionModel.add(linkTo(methodOn(ClienteController.class).registrar(new Cliente())).withRel("registrar"));

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Modifica los datos de un cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente")
    public ResponseEntity<EntityModel<Cliente>> actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        Cliente actualizado = clienteService.actualizar(id, cliente);

        EntityModel<Cliente> model = EntityModel.of(actualizado);
        model.add(linkTo(methodOn(ClienteController.class).obtenerPorId(id)).withSelfRel());
        model.add(linkTo(methodOn(ClienteController.class).listar()).withRel("todos"));
        model.add(linkTo(methodOn(ClienteController.class).eliminar(id)).withRel("eliminar"));

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su ID")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
