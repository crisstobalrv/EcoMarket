package com.ecomarket.reportes.controller;

import com.ecomarket.reportes.external.Pedido;
import com.ecomarket.reportes.external.PedidoEmbeddedWrapper;
import com.ecomarket.reportes.external.Venta;
import com.ecomarket.reportes.external.VentasWrapper;
import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testListarTodos() throws Exception {
        Reporte r1 = Reporte.builder().id(1L).tipo("Tipo 1").datos("{}").build();
        Reporte r2 = Reporte.builder().id(2L).tipo("Tipo 2").datos("{}").build();

        Mockito.when(reporteService.listarTodos()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testObtenerPorId() throws Exception {
        Reporte r = Reporte.builder().id(1L).tipo("Tipo X").datos("{}").build();
        Mockito.when(reporteService.buscarPorId(1L)).thenReturn(r);

        mockMvc.perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("Tipo X"));
    }

    @Test
    void testGenerarReporteVentasPorFecha() throws Exception {
        // Crear wrapper simulado con lista vacía
        VentasWrapper wrapper = new VentasWrapper();
        VentasWrapper.Embedded embedded = new VentasWrapper.Embedded();
        embedded.setVentaList(List.of()); // usa "setVentaList" según tu DTO
        wrapper.set_embedded(embedded);

        // Simular respuesta del microservicio de ventas
        Mockito.when(restTemplate.getForObject(anyString(), eq(VentasWrapper.class))).thenReturn(wrapper);

        // Simular lógica de reporte
        Reporte r = Reporte.builder().id(3L).tipo("Ventas por fecha").datos("{}").build();
        Mockito.when(reporteService.generarReporteVentasPorFecha(any(), any(), anyList())).thenReturn(r);

        // Ejecutar petición con formato correcto de fecha (dd/MM/yyyy)
        mockMvc.perform(get("/api/reportes/ventas?desde=01/01/2024&hasta=31/12/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Ventas por fecha"));
    }



    @Test
    void testGenerarReportePedidosPorEstado() throws Exception {
        Reporte r = Reporte.builder().id(4L).tipo("Pedidos por estado").datos("{}").build();

        PedidoEmbeddedWrapper wrapper = new PedidoEmbeddedWrapper();
        PedidoEmbeddedWrapper.EmbeddedPedidos embedded = new PedidoEmbeddedWrapper.EmbeddedPedidos();
        embedded.setPedidoList(List.of());
        wrapper.set_embedded(embedded);

        Mockito.when(restTemplate.getForObject(anyString(), eq(PedidoEmbeddedWrapper.class))).thenReturn(wrapper);
        Mockito.when(reporteService.generarReportePedidosPorEstado(anyList())).thenReturn(r);

        mockMvc.perform(get("/api/reportes/pedidos/por-estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Pedidos por estado"));
    }

    @Test
    void testGenerarProductosMasVendidos() throws Exception {
        Reporte r = Reporte.builder().id(5L).tipo("Productos más vendidos").datos("{}").build();

        PedidoEmbeddedWrapper wrapper = new PedidoEmbeddedWrapper();
        PedidoEmbeddedWrapper.EmbeddedPedidos embedded = new PedidoEmbeddedWrapper.EmbeddedPedidos();
        embedded.setPedidoList(List.of());
        wrapper.set_embedded(embedded);

        Mockito.when(restTemplate.getForObject(anyString(), eq(PedidoEmbeddedWrapper.class))).thenReturn(wrapper);
        Mockito.when(reporteService.generarReporteProductosMasVendidos(anyList())).thenReturn(r);

        mockMvc.perform(get("/api/reportes/productos-mas-vendidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Productos más vendidos"));
    }
}
