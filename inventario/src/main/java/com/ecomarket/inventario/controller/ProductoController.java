package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Producto producto) {
        Producto guardado = productoService.registrar(producto);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto registrado correctamente",
                "producto", guardado
        ));
    }


    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizarProducto(id, producto);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto actualizado correctamente",
                "producto", actualizado
        ));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        productoService.eliminarPorId(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto eliminado correctamente"
        ));
    }


    @GetMapping("/categoria/{categoria}")
    public List<Producto> obtenerPorCategoria(@PathVariable String categoria) {
        return productoService.buscarPorCategoria(categoria);
    }

    @GetMapping("/{id}/disponibilidad/{cantidad}")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long id,
            @PathVariable Integer cantidad) {

        return productoService.buscarPorId(id)
                .map(producto -> ResponseEntity.ok(producto.getStock() >= cantidad))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/descontar/{cantidad}")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @PathVariable Integer cantidad) {
        productoService.descontarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }

}
