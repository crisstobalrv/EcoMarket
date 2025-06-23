package com.ecomarket.resenas.controller;

import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.service.ResenaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Resena resena = new Resena(2L, 2L, 2L, 5, "Excelente producto", LocalDate.now());

    @Test
    void testListarTodas() throws Exception {
        when(resenaService.listarTodas()).thenReturn(List.of(resena));

        mockMvc.perform(get("/api/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(2));
    }

    @Test
    void testListarPorProducto() throws Exception {
        when(resenaService.listarPorProducto(2L)).thenReturn(List.of(resena));

        mockMvc.perform(get("/api/resenas/producto/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(2));
    }

    @Test
    void testListarPorCliente() throws Exception {
        when(resenaService.listarPorCliente(2L)).thenReturn(List.of(resena));

        mockMvc.perform(get("/api/resenas/cliente/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(2));
    }

    @Test
    void testEditarResena() throws Exception {
        doNothing().when(resenaService).editarDesdeDTO(eq(2L), any());

        mockMvc.perform(put("/api/resenas/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resena)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Reseña actualizada correctamente."));
    }

    @Test
    void testEliminarResena() throws Exception {
        doNothing().when(resenaService).eliminar(1L);

        mockMvc.perform(delete("/api/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Reseña eliminada exitosamente."));
    }
}
