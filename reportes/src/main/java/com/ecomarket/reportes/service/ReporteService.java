package com.ecomarket.reportes.service;

import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.repository.ReporteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository repo;

    public ReporteService(ReporteRepository repo) {
        this.repo = repo;
    }

    public Reporte guardarReporte(String tipo, String datos) {
        Reporte reporte = Reporte.builder()
                .tipo(tipo)
                .fechaGeneracion(LocalDate.now())
                .datos(datos)
                .build();
        return repo.save(reporte);
    }

    public List<Reporte> listarTodos() {
        return repo.findAll();
    }

    public Reporte buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }
}
