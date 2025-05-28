package com.ecomarket.ventas.controller;

import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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
    public ResponseEntity<?> registrar(@RequestBody Venta venta) {
        Venta registrada = ventaService.registrarVenta(venta);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Venta registrada correctamente");
        respuesta.put("venta", registrada);

        return ResponseEntity.ok(respuesta);
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
    public ResponseEntity<?> emitirFactura(@PathVariable Long id) {
        Map<String, Object> factura = ventaService.generarFactura(id);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Factura generada correctamente");
        respuesta.put("factura", factura);

        return ResponseEntity.ok(respuesta);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        boolean anulada = ventaService.anularVenta(id);

        if (anulada) {
            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "Venta anulada correctamente");
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Venta no encontrada o ya anulada"));
        }
    }


}
