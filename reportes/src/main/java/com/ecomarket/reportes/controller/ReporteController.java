package com.ecomarket.reportes.controller;

import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public List<Reporte> listarTodos() {
        return reporteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }

    // Endpoint simulado para generar reporte (real luego usa REST a otros MS)
    @PostMapping("/generar")
    public Reporte generarReporteEjemplo(@RequestParam String tipo) {
        String datosFalsos = """
            {
                "resumen": "Ejemplo de reporte",
                "detalle": [ { "item": "A", "total": 10 } ]
            }
        """;
        return reporteService.guardarReporte(tipo, datosFalsos);
    }
}
