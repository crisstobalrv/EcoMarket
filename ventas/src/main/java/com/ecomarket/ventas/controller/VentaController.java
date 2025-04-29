package com.ecomarket.ventas.controller;

import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public Venta registrar(@RequestBody Venta venta) {
        return ventaService.registrarVenta(venta);
    }

    @GetMapping
    public List<Venta> listarTodas() {
        return ventaService.obtenerTodas();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Venta> listarPorCliente(@PathVariable Long clienteId) {
        return ventaService.obtenerPorCliente(clienteId);
    }


    @GetMapping("/{id}/factura")
    public Map<String, Object> emitirFactura(@PathVariable Long id) {
        return ventaService.generarFactura(id);
    }

}
