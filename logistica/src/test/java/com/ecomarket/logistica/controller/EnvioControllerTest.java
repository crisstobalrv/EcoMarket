package com.ecomarket.logistica.controller;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.service.EnvioService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnvioController.class)
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvioService envioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Envio envio;

    @BeforeEach
    void setUp() {
        envio = new Envio();
        envio.setId(1L);
        envio.setVentaId(10L);
        envio.setEstado("En preparación");
        envio.setFechaEnvio(LocalDate.now());
    }

    @Test
    void testCrearEnvio() throws Exception {
        when(envioService.crearEnvio(any(Envio.class))).thenReturn(envio);

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(envio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Envío creado exitosamente."))
                .andExpect(jsonPath("$.envio.id").value(envio.getId()))
                .andExpect(jsonPath("$.envio.estado").value("En preparación"));
    }

    @Test
    void testListarTodos() throws Exception {
        when(envioService.obtenerTodos()).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(envio.getId()))
                .andExpect(jsonPath("$[0].estado").value("En preparación"));
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        when(envioService.obtenerPorId(1L)).thenReturn(Optional.of(envio));

        mockMvc.perform(get("/api/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("En preparación"));
    }

    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        when(envioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/envios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerPorVenta() throws Exception {
        when(envioService.obtenerPorVenta(10L)).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/envios/venta/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ventaId").value(10));
    }

    @Test
    void testActualizarEstadoExitoso() throws Exception {
        envio.setEstado("En camino");
        when(envioService.actualizarEstado(eq(1L), eq("En camino"))).thenReturn(envio);

        mockMvc.perform(patch("/api/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "En camino"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estado del envío actualizado correctamente."))
                .andExpect(jsonPath("$.nuevoEstado").value("En camino"));
    }

    @Test
    void testActualizarEstadoNoEncontrado() throws Exception {
        when(envioService.actualizarEstado(eq(99L), eq("En camino")))
                .thenThrow(new RuntimeException("Envío no encontrado"));

        mockMvc.perform(patch("/api/envios/99/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "En camino"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Envío no encontrado"));
    }
}
