package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones para gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Guardar nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado correctamente")
    @PostMapping
    public ResponseEntity<EntityModel<Producto>> guardarProducto(@RequestBody Producto producto) {
        Producto creado = productoService.guardarProducto(producto);
        EntityModel<Producto> model = EntityModel.of(creado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(creado.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos"));
        return ResponseEntity.status(201).body(model);
    }



    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.obtenerTodos().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(ProductoController.class).obtenerProductoPorId(p.getId())).withSelfRel(),
                        linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(productos));
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerProductoPorId(id);
            EntityModel<Producto> model = EntityModel.of(producto,
                    linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel(),
                    linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos"));
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar producto por ID")
    @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizarProducto(id, producto);

        EntityModel<Producto> model = EntityModel.of(actualizado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos"));

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Buscar productos por categoría")
    @ApiResponse(responseCode = "200", description = "Productos encontrados para la categoría")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> obtenerPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.buscarPorCategoria(categoria);

        List<EntityModel<Producto>> modelos = productos.stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(ProductoController.class).obtenerProductoPorId(p.getId())).withSelfRel(),
                        linkTo(methodOn(ProductoController.class).obtenerPorCategoria(categoria)).withRel("porCategoria")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(modelos));
    }

    @Operation(summary = "Verificar disponibilidad de stock")
    @ApiResponse(responseCode = "200", description = "Disponibilidad evaluada")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}/disponibilidad/{cantidad}")
    public ResponseEntity<EntityModel<Boolean>> verificarDisponibilidad(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {
        try {
            Producto producto = productoService.obtenerProductoPorId(id);
            boolean disponible = producto.getStock() >= cantidad;

            EntityModel<Boolean> model = EntityModel.of(disponible,
                    linkTo(methodOn(ProductoController.class).verificarDisponibilidad(id, cantidad)).withSelfRel(),
                    linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withRel("producto"));

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Descontar stock de producto")
    @ApiResponse(responseCode = "200", description = "Stock descontado correctamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PutMapping("/{id}/descontar/{cantidad}")
    public ResponseEntity<EntityModel<String>> descontarStock(@PathVariable Long id, @PathVariable Integer cantidad) {
        productoService.descontarStock(id, cantidad);

        EntityModel<String> model = EntityModel.of("Stock descontado",
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withRel("producto"),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos"));

        return ResponseEntity.ok(model);
    }


}
