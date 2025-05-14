package com.ecomarket.reportes.controller;

import com.ecomarket.reportes.external.Pedido;
import com.ecomarket.reportes.external.Venta;
import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final RestTemplate restTemplate;

    public ReporteController(ReporteService reporteService, RestTemplate restTemplate) {
        this.reporteService = reporteService;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Reporte> listarTodos() {
        return reporteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }

    @GetMapping("/ventas")
    public Reporte generarReporteVentasPorFecha(@RequestParam String desde, @RequestParam String hasta) {
        String urlVentas = "http://localhost:8084/api/ventas";
        Venta[] ventasTotales = restTemplate.getForObject(urlVentas, Venta[].class);

        LocalDate d = LocalDate.parse(desde);
        LocalDate h = LocalDate.parse(hasta);

        List<Venta> filtradas = Arrays.stream(ventasTotales)
                .filter(v -> !v.getFechaVenta().isBefore(d) && !v.getFechaVenta().isAfter(h))
                .toList();

        return reporteService.generarReporteVentasPorFecha(d, h, filtradas);
    }

    @GetMapping("/pedidos/por-estado")
    public Reporte generarReportePedidosPorEstado() {
        String url = "http://localhost:8089/api/pedidos";
        Pedido[] pedidos = restTemplate.getForObject(url, Pedido[].class);
        List<Pedido> lista = Arrays.asList(pedidos);
        return reporteService.generarReportePedidosPorEstado(lista);
    }

    @GetMapping("/productos-mas-vendidos")
    public Reporte generarProductosMasVendidos() {
        String url = "http://localhost:8089/api/pedidos";
        Pedido[] pedidos = restTemplate.getForObject(url, Pedido[].class);
        List<Pedido> lista = Arrays.asList(pedidos);
        return reporteService.generarReporteProductosMasVendidos(lista);
    }

}
