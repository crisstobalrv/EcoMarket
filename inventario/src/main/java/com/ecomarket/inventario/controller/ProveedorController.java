package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Operaciones CRUD sobre proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @PostMapping
    @Operation(summary = "Registrar proveedor")
    @ApiResponse(responseCode = "201", description = "Proveedor creado")
    public ResponseEntity<EntityModel<Proveedor>> registrar(@RequestBody Proveedor proveedor) {
        Proveedor creado = proveedorService.guardarProveedor(proveedor);

        EntityModel<Proveedor> model = EntityModel.of(creado,
                linkTo(methodOn(ProveedorController.class).obtenerProveedorPorId(creado.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).listarProveedores()).withRel("todos")
        );

        return ResponseEntity.status(201).body(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<EntityModel<Proveedor>> obtenerProveedorPorId(@PathVariable Long id) {
        try {
            Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
            EntityModel<Proveedor> model = EntityModel.of(proveedor,
                    linkTo(methodOn(ProveedorController.class).obtenerProveedorPorId(id)).withSelfRel(),
                    linkTo(methodOn(ProveedorController.class).listarProveedores()).withRel("todos")
            );
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los proveedores")
    @ApiResponse(responseCode = "200", description = "Listado de proveedores")
    public ResponseEntity<CollectionModel<EntityModel<Proveedor>>> listarProveedores() {
        List<Proveedor> proveedores = proveedorService.obtenerTodos();

        List<EntityModel<Proveedor>> modelos = proveedores.stream().map(p ->
                EntityModel.of(p,
                        linkTo(methodOn(ProveedorController.class).obtenerProveedorPorId(p.getId())).withSelfRel()
                )
        ).toList();

        return ResponseEntity.ok(CollectionModel.of(modelos,
                linkTo(methodOn(ProveedorController.class).listarProveedores()).withSelfRel()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor")
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado")
    public ResponseEntity<EntityModel<Proveedor>> actualizarProveedor(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        Proveedor actualizado = proveedorService.actualizarProveedor(id, proveedor);

        EntityModel<Proveedor> model = EntityModel.of(actualizado,
                linkTo(methodOn(ProveedorController.class).obtenerProveedorPorId(id)).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).listarProveedores()).withRel("todos")
        );

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor")
    @ApiResponse(responseCode = "204", description = "Proveedor eliminado")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
