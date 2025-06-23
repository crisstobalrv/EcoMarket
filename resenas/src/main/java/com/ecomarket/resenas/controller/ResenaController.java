package com.ecomarket.resenas.controller;

import com.ecomarket.resenas.dto.CrearResenaDTO;
import com.ecomarket.resenas.dto.EditarResenaDTO;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @Operation(summary = "Crear nueva reseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de entrada")
    })
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearResenaDTO dto) {
        try {
            Resena creada = resenaService.crearDesdeDTO(dto);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("mensaje", "Reseña creada correctamente");
            response.put("resena", EntityModel.of(creada,
                    linkTo(methodOn(ResenaController.class).listarTodas()).withRel("todas"),
                    linkTo(methodOn(ResenaController.class).listarPorProducto(creada.getProductoId())).withRel("por_producto"),
                    linkTo(methodOn(ResenaController.class).listarPorCliente(creada.getClienteId())).withRel("por_cliente"),
                    linkTo(methodOn(ResenaController.class).eliminar(creada.getId())).withRel("eliminar")
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Listar todas las reseñas")
    @GetMapping
    public CollectionModel<EntityModel<Resena>> listarTodas() {
        List<EntityModel<Resena>> modelos = resenaService.listarTodas().stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ResenaController.class).editar(r.getId(), null)).withRel("editar"),
                        linkTo(methodOn(ResenaController.class).eliminar(r.getId())).withRel("eliminar")
                )).collect(Collectors.toList());

        return CollectionModel.of(modelos, linkTo(methodOn(ResenaController.class).listarTodas()).withSelfRel());
    }

    @Operation(summary = "Listar reseñas por producto")
    @GetMapping("/producto/{productoId}")
    public CollectionModel<EntityModel<Resena>> listarPorProducto(@PathVariable Long productoId) {
        List<EntityModel<Resena>> modelos = resenaService.listarPorProducto(productoId).stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ResenaController.class).editar(r.getId(), null)).withRel("editar"),
                        linkTo(methodOn(ResenaController.class).eliminar(r.getId())).withRel("eliminar")
                )).collect(Collectors.toList());

        return CollectionModel.of(modelos, linkTo(methodOn(ResenaController.class).listarPorProducto(productoId)).withSelfRel());
    }

    @Operation(summary = "Listar reseñas por cliente")
    @GetMapping("/cliente/{clienteId}")
    public CollectionModel<EntityModel<Resena>> listarPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<Resena>> modelos = resenaService.listarPorCliente(clienteId).stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ResenaController.class).editar(r.getId(), null)).withRel("editar"),
                        linkTo(methodOn(ResenaController.class).eliminar(r.getId())).withRel("eliminar")
                )).collect(Collectors.toList());

        return CollectionModel.of(modelos, linkTo(methodOn(ResenaController.class).listarPorCliente(clienteId)).withSelfRel());
    }

    @Operation(summary = "Editar una reseña existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody EditarResenaDTO dto) {
        resenaService.editarDesdeDTO(id, dto);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña actualizada correctamente."));
    }

    @Operation(summary = "Eliminar reseña por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Reseña eliminada exitosamente."));
    }

    @Operation(summary = "Obtener reseña por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return resenaService.buscarPorId(id)
                .map(resena -> {
                    EntityModel<Resena> model = EntityModel.of(resena,
                            linkTo(methodOn(ResenaController.class).buscarPorId(id)).withSelfRel(),
                            linkTo(methodOn(ResenaController.class).listarTodas()).withRel("todas"),
                            linkTo(methodOn(ResenaController.class).listarPorProducto(resena.getProductoId())).withRel("por_producto"),
                            linkTo(methodOn(ResenaController.class).listarPorCliente(resena.getClienteId())).withRel("por_cliente"),
                            linkTo(methodOn(ResenaController.class).editar(id, null)).withRel("editar"),
                            linkTo(methodOn(ResenaController.class).eliminar(id)).withRel("eliminar")
                    );
                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
