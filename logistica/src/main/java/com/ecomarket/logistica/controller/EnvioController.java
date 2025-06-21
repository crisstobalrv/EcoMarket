package com.ecomarket.logistica.controller;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios", description = "API para gestionar envíos de productos")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo envío")
    public ResponseEntity<EntityModel<Envio>> crearEnvio(@RequestBody Envio envio) {
        Envio creado = envioService.crearEnvio(envio);

        EntityModel<Envio> model = EntityModel.of(creado,
                linkTo(methodOn(EnvioController.class).obtenerPorId(creado.getId())).withSelfRel(),
                linkTo(methodOn(EnvioController.class).listarTodos()).withRel("todos"),
                linkTo(methodOn(EnvioController.class).actualizarEstado(creado.getId(), Map.of("estado", "En camino"))).withRel("actualizar_estado"),
                linkTo(methodOn(EnvioController.class).obtenerPorVenta(creado.getVentaId())).withRel("por_venta")
        );

        return ResponseEntity.created(linkTo(methodOn(EnvioController.class).obtenerPorId(creado.getId())).toUri())
                .body(model);
    }

    @GetMapping
    @Operation(summary = "Listar todos los envíos")
    public ResponseEntity<CollectionModel<EntityModel<Envio>>> listarTodos() {
        List<EntityModel<Envio>> envios = envioService.obtenerTodos().stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(EnvioController.class).obtenerPorId(envio.getId())).withSelfRel(),
                        linkTo(methodOn(EnvioController.class).actualizarEstado(envio.getId(), Map.of("estado", "En camino"))).withRel("actualizar_estado"),
                        linkTo(methodOn(EnvioController.class).obtenerPorVenta(envio.getVentaId())).withRel("por_venta")
                ))
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(envios, linkTo(methodOn(EnvioController.class).listarTodos()).withSelfRel())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un envío por su ID")
    public ResponseEntity<EntityModel<Envio>> obtenerPorId(@PathVariable Long id) {
        return envioService.obtenerPorId(id)
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(EnvioController.class).obtenerPorId(id)).withSelfRel(),
                        linkTo(methodOn(EnvioController.class).listarTodos()).withRel("todos"),
                        linkTo(methodOn(EnvioController.class).actualizarEstado(id, Map.of("estado", "En camino"))).withRel("actualizar_estado"),
                        linkTo(methodOn(EnvioController.class).obtenerPorVenta(envio.getVentaId())).withRel("por_venta")
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Obtener envíos por ID de venta")
    public ResponseEntity<CollectionModel<EntityModel<Envio>>> obtenerPorVenta(@PathVariable Long ventaId) {
        List<EntityModel<Envio>> envios = envioService.obtenerPorVenta(ventaId).stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(EnvioController.class).obtenerPorId(envio.getId())).withSelfRel(),
                        linkTo(methodOn(EnvioController.class).actualizarEstado(envio.getId(), Map.of("estado", "En camino"))).withRel("actualizar_estado")
                ))
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(envios, linkTo(methodOn(EnvioController.class).obtenerPorVenta(ventaId)).withSelfRel())
        );
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de un envío")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> estado) {
        try {
            Envio actualizado = envioService.actualizarEstado(id, estado.get("estado"));

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "Estado del envío actualizado correctamente.");
            respuesta.put("nuevoEstado", actualizado.getEstado());
            respuesta.put("links", List.of(
                    linkTo(methodOn(EnvioController.class).obtenerPorId(id)).withRel("self").getHref(),
                    linkTo(methodOn(EnvioController.class).listarTodos()).withRel("todos").getHref(),
                    linkTo(methodOn(EnvioController.class).obtenerPorVenta(actualizado.getVentaId())).withRel("por_venta").getHref()
            ));

            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
