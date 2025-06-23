package com.ecomarket.ventas.controller;

import com.ecomarket.ventas.dto.CrearVentaDTO;
import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Gestionar ventas de productos")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Registrar una nueva venta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o venta duplicada")
    })
    @PostMapping
    public ResponseEntity<?> registrarVentaDesdeDTO(@RequestBody CrearVentaDTO dto) {
        Venta registrada = ventaService.registrarVentaDesdeDTO(dto);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Venta registrada correctamente");
        respuesta.put("venta", EntityModel.of(registrada,
                linkTo(methodOn(VentaController.class).obtenerPorId(registrada.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarPorCliente(registrada.getClienteId())).withRel("por_cliente"),
                linkTo(methodOn(VentaController.class).emitirFactura(registrada.getId())).withRel("factura"),
                linkTo(methodOn(VentaController.class).anularVenta(registrada.getId())).withRel("anular")
        ));

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar todas las ventas")
    @GetMapping
    public CollectionModel<EntityModel<Venta>> listarTodas() {
        List<EntityModel<Venta>> ventas = ventaService.obtenerTodas().stream()
                .map(v -> EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).obtenerPorId(v.getId())).withSelfRel(),
                        linkTo(methodOn(VentaController.class).emitirFactura(v.getId())).withRel("factura"),
                        linkTo(methodOn(VentaController.class).anularVenta(v.getId())).withRel("anular")
                )).collect(Collectors.toList());

        return CollectionModel.of(ventas, linkTo(methodOn(VentaController.class).listarTodas()).withSelfRel());
    }

    @Operation(summary = "Listar ventas por cliente")
    @GetMapping("/cliente/{clienteId}")
    public CollectionModel<EntityModel<Venta>> listarPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<Venta>> ventas = ventaService.obtenerPorCliente(clienteId).stream()
                .map(v -> EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).emitirFactura(v.getId())).withRel("factura"),
                        linkTo(methodOn(VentaController.class).anularVenta(v.getId())).withRel("anular")
                )).collect(Collectors.toList());

        return CollectionModel.of(ventas, linkTo(methodOn(VentaController.class).listarPorCliente(clienteId)).withSelfRel());
    }

    @Operation(summary = "Generar factura de una venta")
    @GetMapping("/{id}/factura")
    public ResponseEntity<?> emitirFactura(@PathVariable Long id) {
        Map<String, Object> factura = ventaService.generarFactura(id);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Factura generada correctamente");
        respuesta.put("factura", factura);

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Obtener venta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(v -> ResponseEntity.ok(EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).obtenerPorId(id)).withSelfRel(),
                        linkTo(methodOn(VentaController.class).listarPorCliente(v.getClienteId())).withRel("por_cliente"),
                        linkTo(methodOn(VentaController.class).emitirFactura(id)).withRel("factura"),
                        linkTo(methodOn(VentaController.class).anularVenta(id)).withRel("anular")
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Anular una venta")
    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        boolean anulada = ventaService.anularVenta(id);

        if (anulada) {
            return ResponseEntity.ok(Map.of("mensaje", "Venta anulada correctamente"));
        } else {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Venta no encontrada o ya anulada"));
        }
    }
}
