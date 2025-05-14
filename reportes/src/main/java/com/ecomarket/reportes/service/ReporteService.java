package com.ecomarket.reportes.service;

import com.ecomarket.reportes.external.DetallePedido;
import com.ecomarket.reportes.external.Pedido;
import com.ecomarket.reportes.external.Producto;
import com.ecomarket.reportes.external.Venta;
import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.repository.ReporteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final ReporteRepository repo;
    private final RestTemplate restTemplate;

    public ReporteService(ReporteRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }


    public Reporte guardarReporte(String tipo, String datos) {
        Reporte reporte = Reporte.builder()
                .tipo(tipo)
                .fechaGeneracion(LocalDate.now())
                .datos(datos)
                .build();
        return repo.save(reporte);
    }

    public Reporte generarReporteVentasPorFecha(LocalDate desde, LocalDate hasta, List<Venta> ventas) {
        double total = ventas.stream()
                .mapToDouble(Venta::getTotalVenta)
                .sum();

        Map<String, Object> reporteData = new LinkedHashMap<>();
        reporteData.put("fechaDesde", desde);
        reporteData.put("fechaHasta", hasta);
        reporteData.put("totalVendido", total);
        reporteData.put("ventas", ventas);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String datosJson = mapper.writeValueAsString(reporteData);
            return guardarReporte("Ventas por fecha", datosJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al generar JSON del reporte", e);
        }
    }

    public Reporte generarReportePedidosPorEstado(List<Pedido> pedidos) {
        Map<String, Long> conteo = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getEstado, Collectors.counting()));

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("fechaGeneracion", LocalDate.now());
        resumen.put("conteoPorEstado", conteo);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String datosJson = mapper.writeValueAsString(resumen);

            return guardarReporte("Pedidos por estado", datosJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al generar JSON del reporte", e);
        }
    }

    public Reporte generarReporteProductosMasVendidos(List<Pedido> pedidos) {
        Map<Long, Integer> acumulador = new HashMap<>();

        for (Pedido pedido : pedidos) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                acumulador.merge(detalle.getProductoId(), detalle.getCantidad(), Integer::sum);
            }
        }

        // Obtener nombres de productos desde Inventario (por REST)
        List<Map<String, Object>> reporteFinal = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : acumulador.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidadVendida = entry.getValue();

            String urlProducto = "http://localhost:8083/api/productos/" + productoId;
            Producto producto = restTemplate.getForObject(urlProducto, Producto.class);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productoId", productoId);
            item.put("nombre", producto != null ? producto.getNombre() : "Desconocido");
            item.put("cantidadVendida", cantidadVendida);

            reporteFinal.add(item);
        }

        // Ordenar por cantidad vendida descendente
        reporteFinal.sort((a, b) -> ((Integer)b.get("cantidadVendida")).compareTo((Integer)a.get("cantidadVendida")));

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("fechaGeneracion", LocalDate.now());
        resumen.put("productosMasVendidos", reporteFinal);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String datosJson = mapper.writeValueAsString(resumen);
            return guardarReporte("Productos más vendidos", datosJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al generar JSON del reporte", e);
        }
    }




    public List<Reporte> listarTodos() {
        return repo.findAll();
    }

    public Reporte buscarPorId(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }
}
