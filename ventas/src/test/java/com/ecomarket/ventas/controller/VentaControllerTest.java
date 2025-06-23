package com.ecomarket.ventas.controller;

import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(VentaController.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setId(1L);
        venta.setClienteId(5L);
        venta.setFechaVenta(LocalDate.now());
        venta.setTotalVenta(10000.0);
    }

    @Test
    void testRegistrarVenta() throws Exception {
        when(ventaService.registrarVentaDesdeDTO(any())).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "clienteId": 5,
                          "total": 10000
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Venta registrada correctamente")))
                .andExpect(jsonPath("$.venta.clienteId", is(5)));
    }

    @Test
    void testListarTodas() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testListarPorCliente() throws Exception {
        when(ventaService.obtenerPorCliente(5L)).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas/cliente/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId", is(5)));
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(ventaService.buscarPorId(1L)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testEmitirFactura() throws Exception {
        when(ventaService.generarFactura(1L)).thenReturn(Map.of("cliente", "Juan"));

        mockMvc.perform(get("/api/ventas/1/factura"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Factura generada correctamente")))
                .andExpect(jsonPath("$.factura.cliente", is("Juan")));
    }

    @Test
    void testAnularVenta() throws Exception {
        when(ventaService.anularVenta(1L)).thenReturn(true);

        mockMvc.perform(patch("/api/ventas/1/anular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Venta anulada correctamente")));
    }

    @Test
    void testAnularVentaNoExiste() throws Exception {
        when(ventaService.anularVenta(999L)).thenReturn(false);

        mockMvc.perform(patch("/api/ventas/999/anular"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Venta no encontrada o ya anulada")));
    }
}
