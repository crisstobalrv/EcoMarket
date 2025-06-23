package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.dto.EstadoDTO;
import com.ecomarket.pedidos.dto.PedidoRequestDTO;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos de clientes")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    public ResponseEntity<EntityModel<Pedido>> crearPedido(@RequestBody PedidoRequestDTO pedidoDTO) {
        // Convertimos el DTO a entidad real (usa un nuevo método en el servicio)
        Pedido nuevo = pedidoService.registrarDesdeDto(pedidoDTO);

        EntityModel<Pedido> recurso = EntityModel.of(nuevo,
                linkTo(methodOn(PedidoController.class).obtenerPedido(nuevo.getId())).withSelfRel(),
                linkTo(methodOn(PedidoController.class).listarTodos()).withRel("todos"),
                linkTo(methodOn(PedidoController.class).cambiarEstado(nuevo.getId(), new EstadoDTO("En camino"))).withRel("actualizar_estado"),
                linkTo(methodOn(PedidoController.class).obtenerPorCliente(nuevo.getClienteId())).withRel("por_cliente"),
                linkTo(methodOn(PedidoController.class).eliminarPorId(nuevo.getId())).withRel("eliminar")
        );

        return ResponseEntity.created(linkTo(PedidoController.class).slash(nuevo.getId()).toUri())
                .body(recurso);
    }


    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> listarTodos() {
        EstadoDTO ejemploEstado = new EstadoDTO("En camino");

        List<EntityModel<Pedido>> recursos = pedidoService.listarTodos().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PedidoController.class).obtenerPedido(p.getId())).withSelfRel(),
                        linkTo(methodOn(PedidoController.class).cambiarEstado(p.getId(), ejemploEstado)).withRel("actualizar_estado"),
                        linkTo(methodOn(PedidoController.class).obtenerPorCliente(p.getClienteId())).withRel("por_cliente"),
                        linkTo(methodOn(PedidoController.class).eliminarPorId(p.getId())).withRel("eliminar")
                )).toList();

        return ResponseEntity.ok(
                CollectionModel.of(recursos, linkTo(methodOn(PedidoController.class).listarTodos()).withSelfRel())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<EntityModel<Pedido>> obtenerPedido(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(p -> {
                    EstadoDTO estadoEjemplo = new EstadoDTO();
                    estadoEjemplo.setEstado("En camino");

                    return EntityModel.of(p,
                            linkTo(methodOn(PedidoController.class).obtenerPedido(id)).withSelfRel(),
                            linkTo(methodOn(PedidoController.class).listarTodos()).withRel("todos"),
                            linkTo(methodOn(PedidoController.class).obtenerPorCliente(p.getClienteId())).withRel("por_cliente"),
                            linkTo(methodOn(PedidoController.class).cambiarEstado(id, estadoEjemplo)).withRel("actualizar_estado"),
                            linkTo(methodOn(PedidoController.class).eliminarPorId(id)).withRel("eliminar")
                    );
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un pedido")
    public ResponseEntity<EntityModel<Pedido>> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoDTO estadoDto) {

        Pedido actualizado = pedidoService.actualizarEstado(id, estadoDto.getEstado());

        EntityModel<Pedido> recurso = EntityModel.of(actualizado,
                linkTo(methodOn(PedidoController.class).obtenerPedido(id)).withSelfRel(),
                linkTo(methodOn(PedidoController.class).listarTodos()).withRel("todos"),
                linkTo(methodOn(PedidoController.class).obtenerPorCliente(actualizado.getClienteId())).withRel("por_cliente")
        );

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido por ID")
    public ResponseEntity<Map<String, Object>> eliminarPorId(@PathVariable Long id) {
        pedidoService.eliminarPorId(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "El pedido fue eliminado correctamente.");
        response.put("pedidoId", id);
        response.put("links", List.of(
                linkTo(methodOn(PedidoController.class).listarTodos()).withRel("todos").getHref()
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos por cliente")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorCliente(@PathVariable Long clienteId) {
        EstadoDTO ejemploEstado = new EstadoDTO("En camino");

        List<EntityModel<Pedido>> recursos = pedidoService.buscarPorCliente(clienteId).stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PedidoController.class).obtenerPedido(p.getId())).withSelfRel(),
                        linkTo(methodOn(PedidoController.class).cambiarEstado(p.getId(), ejemploEstado)).withRel("actualizar_estado"),
                        linkTo(methodOn(PedidoController.class).eliminarPorId(p.getId())).withRel("eliminar")
                )).toList();

        return ResponseEntity.ok(
                CollectionModel.of(recursos, linkTo(methodOn(PedidoController.class).obtenerPorCliente(clienteId)).withSelfRel())
        );
    }
}
