package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.DisponibilidadResponse;
import com.ecomarket.inventario.dto.MensajeResponse;
import com.ecomarket.inventario.dto.ProductoCreateRequest;
import com.ecomarket.inventario.dto.ProductoUpdateRequest;
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
    public ResponseEntity<EntityModel<Producto>> guardarProducto(@RequestBody ProductoCreateRequest request) {
        Producto creado = productoService.guardarProducto(request);

        EntityModel<Producto> model = EntityModel.of(creado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(creado.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                linkTo(methodOn(ProductoController.class).actualizar(creado.getId(), new ProductoUpdateRequest())).withRel("Actualizar producto"),
                linkTo(methodOn(ProductoController.class).eliminarProducto(creado.getId())).withRel("Eliminar producto"),
                linkTo(methodOn(ProductoController.class).obtenerPorCategoria(creado.getCategoria())).withRel("Listar por categoria"),
                linkTo(methodOn(ProductoController.class).verificarDisponibilidad(creado.getId(), 1)).withRel("Verificar disponibilidad"),
                linkTo(methodOn(ProductoController.class).descontarStock(creado.getId(), 1)).withRel("Descontar stock de un producto")
        );

        return ResponseEntity.status(201).body(model);
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.obtenerTodos().stream()
                .map(producto -> {
                    ProductoUpdateRequest updateRequest = new ProductoUpdateRequest(); // o construirlo si es necesario
                    return EntityModel.of(producto,
                            linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withRel("Ver producto por ID"),
                            linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                            linkTo(methodOn(ProductoController.class).actualizar(producto.getId(), updateRequest)).withRel("Actualizar producto"),
                            linkTo(methodOn(ProductoController.class).eliminarProducto(producto.getId())).withRel("Eliminar producto"),
                            linkTo(methodOn(ProductoController.class).obtenerPorCategoria(producto.getCategoria())).withRel("Listar por categoría"),
                            linkTo(methodOn(ProductoController.class).verificarDisponibilidad(producto.getId(), 1)).withRel("Verificar disponibilidad"),
                            linkTo(methodOn(ProductoController.class).descontarStock(producto.getId(), 1)).withRel("Descontar stock de un producto")
                    );
                })
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
            ProductoUpdateRequest updateRequest = new ProductoUpdateRequest();
            EntityModel<Producto> model = EntityModel.of(producto,
                    linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel(),
                    linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                    linkTo(methodOn(ProductoController.class).actualizar(producto.getId(), updateRequest)).withRel("Actualizar producto"),
                    linkTo(methodOn(ProductoController.class).eliminarProducto(id)).withRel("Eliminar producto"),
                    linkTo(methodOn(ProductoController.class).obtenerPorCategoria(producto.getCategoria())).withRel("Listar por categoria"),
                    linkTo(methodOn(ProductoController.class).verificarDisponibilidad(id, 1)).withRel("Verificar disponibilidad"),
                    linkTo(methodOn(ProductoController.class).descontarStock(id, 1)).withRel("Descontar stock de un producto")
            );
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

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente")
    @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProductoUpdateRequest request) {
        Producto actualizado = productoService.actualizarProducto(id, request);

        EntityModel<Producto> productoModel = EntityModel.of(actualizado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(actualizado.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                linkTo(methodOn(ProductoController.class).obtenerPorCategoria(actualizado.getCategoria())).withRel("Listar por categoria"),
                linkTo(methodOn(ProductoController.class).descontarStock(actualizado.getId(), 1)).withRel("Descontar stock"),
                linkTo(methodOn(ProductoController.class).verificarDisponibilidad(actualizado.getId(), 1)).withRel("Verificar disponibilidad")
                );

        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto actualizado correctamente",
                "producto", productoModel
        ));
    }

    @Operation(summary = "Buscar productos por categoría")
    @ApiResponse(responseCode = "200", description = "Productos encontrados para la categoría")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> obtenerPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.buscarPorCategoria(categoria);

        List<EntityModel<Producto>> modelos = productos.stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(ProductoController.class).obtenerProductoPorId(p.getId())).withRel("Obtener producto"),
                        linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                        linkTo(methodOn(ProductoController.class).actualizar(p.getId(), new ProductoUpdateRequest())).withRel("Actualizar producto"),
                        linkTo(methodOn(ProductoController.class).eliminarProducto(p.getId())).withRel("Eliminar producto"),
                        linkTo(methodOn(ProductoController.class).verificarDisponibilidad(p.getId(), 1)).withRel("Verificar disponibilidad"),
                        linkTo(methodOn(ProductoController.class).descontarStock(p.getId(), 1)).withRel("Descontar stock del producto")
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(modelos,
                        linkTo(methodOn(ProductoController.class).obtenerPorCategoria(categoria)).withSelfRel(),
                        linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos")
                ));
    }


    @GetMapping("/{id}/disponibilidad/{cantidad}")
    @Operation(summary = "Verificar disponibilidad de stock")
    @ApiResponse(responseCode = "200", description = "Disponibilidad evaluada")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<EntityModel<DisponibilidadResponse>> verificarDisponibilidad(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {
        try {
            Producto producto = productoService.obtenerProductoPorId(id);
            boolean disponible = producto.getStock() >= cantidad;

            DisponibilidadResponse response = new DisponibilidadResponse(disponible);

            EntityModel<DisponibilidadResponse> model = EntityModel.of(response,
                    linkTo(methodOn(ProductoController.class).verificarDisponibilidad(id, cantidad)).withSelfRel(),
                    linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withRel("Obtener producto"),
                    linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                    linkTo(methodOn(ProductoController.class).actualizar(id, new ProductoUpdateRequest())).withRel("Actualizar producto"),
                    linkTo(methodOn(ProductoController.class).eliminarProducto(id)).withRel("Eliminar producto"),
                    linkTo(methodOn(ProductoController.class).descontarStock(id, cantidad)).withRel("Descontar stock del producto"));

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/descontar/{cantidad}")
    @Operation(summary = "Descontar stock de producto")
    @ApiResponse(responseCode = "200", description = "Stock descontado correctamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<EntityModel<MensajeResponse>> descontarStock(@PathVariable Long id, @PathVariable Integer cantidad) {
        productoService.descontarStock(id, cantidad);

        MensajeResponse respuesta = new MensajeResponse("Stock descontado");

        EntityModel<MensajeResponse> model = EntityModel.of(respuesta,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withRel("Obtener producto por id"),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("Listar todos los productos"),
                linkTo(methodOn(ProductoController.class).verificarDisponibilidad(id, 1)).withRel("Verificar disponibilidad"),
                linkTo(methodOn(ProductoController.class).actualizar(id, new ProductoUpdateRequest())).withRel("Actualizar producto"),
                linkTo(methodOn(ProductoController.class).eliminarProducto(id)).withRel("Eliminar producto")
        );

        return ResponseEntity.ok(model);
    }




}
