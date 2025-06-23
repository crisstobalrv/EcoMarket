package com.ecomarket.reportes.controller;

import com.ecomarket.reportes.external.Pedido;
import com.ecomarket.reportes.external.PedidoEmbeddedWrapper;
import com.ecomarket.reportes.external.Venta;
import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Operaciones relacionadas con reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final RestTemplate restTemplate;

    public ReporteController(ReporteService reporteService, RestTemplate restTemplate) {
        this.reporteService = reporteService;
        this.restTemplate = restTemplate;
    }

    @Operation(summary = "Listar todos los reportes generados")
    @GetMapping
    public CollectionModel<EntityModel<Reporte>> listarTodos() {
        List<Reporte> reportes = reporteService.listarTodos();

        List<EntityModel<Reporte>> recursos = reportes.stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(ReporteController.class).obtenerPorId(r.getId())).withSelfRel(),
                        linkTo(methodOn(ReporteController.class).listarTodos()).withRel("todos")
                )).toList();

        return CollectionModel.of(recursos, linkTo(methodOn(ReporteController.class).listarTodos()).withSelfRel());
    }

    @Operation(summary = "Obtener un reporte por su ID")
    @GetMapping("/{id}")
    public EntityModel<Reporte> obtenerPorId(
            @Parameter(description = "ID del reporte") @PathVariable Long id) {

        Reporte reporte = reporteService.buscarPorId(id);

        return EntityModel.of(reporte,
                linkTo(methodOn(ReporteController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarTodos()).withRel("todos")
        );
    }

    @GetMapping("/ventas")
    @Operation(summary = "Generar reporte de ventas por fecha")
    public EntityModel<Reporte> generarReporteVentasPorFecha(@RequestParam String desde, @RequestParam String hasta) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate d = LocalDate.parse(desde, formatter);
        LocalDate h = LocalDate.parse(hasta, formatter);

        String urlVentas = "http://localhost:8084/api/ventas";
        Venta[] ventasTotales = restTemplate.getForObject(urlVentas, Venta[].class);

        List<Venta> filtradas = Arrays.stream(ventasTotales)
                .filter(v -> v.getFecha() != null)
                .filter(v -> !v.getFecha().isBefore(d) && !v.getFecha().isAfter(h))
                .toList();


        Reporte reporte = reporteService.generarReporteVentasPorFecha(d, h, filtradas);

        return EntityModel.of(reporte,
                linkTo(methodOn(ReporteController.class).generarReporteVentasPorFecha(desde, hasta)).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarTodos()).withRel("todos")
        );
    }


    @Operation(summary = "Generar reporte de pedidos por estado")
    @GetMapping("/pedidos/por-estado")
    public EntityModel<Reporte> generarReportePedidosPorEstado() {
        String url = "http://localhost:8089/api/pedidos";

        PedidoEmbeddedWrapper response = restTemplate.getForObject(url, PedidoEmbeddedWrapper.class);
        List<Pedido> lista = response.get_embedded().getPedidoList();

        Reporte reporte = reporteService.generarReportePedidosPorEstado(lista);

        return EntityModel.of(reporte,
                linkTo(methodOn(ReporteController.class).generarReportePedidosPorEstado()).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarTodos()).withRel("todos")
        );
    }


    @Operation(summary = "Generar reporte de productos más vendidos")
    @GetMapping("/productos-mas-vendidos")
    public EntityModel<Reporte> generarProductosMasVendidos() {
        String url = "http://localhost:8089/api/pedidos";

        PedidoEmbeddedWrapper response = restTemplate.getForObject(url, PedidoEmbeddedWrapper.class);
        List<Pedido> lista = response.get_embedded().getPedidoList();

        Reporte reporte = reporteService.generarReporteProductosMasVendidos(lista);

        return EntityModel.of(reporte,
                linkTo(methodOn(ReporteController.class).generarProductosMasVendidos()).withSelfRel(),
                linkTo(methodOn(ReporteController.class).listarTodos()).withRel("todos")
        );
    }

}
