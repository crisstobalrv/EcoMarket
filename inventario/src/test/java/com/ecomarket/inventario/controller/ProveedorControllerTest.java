package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.service.ProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProveedorController.class)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarProveedor() throws Exception {
        Proveedor proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Distribuidora Sur")
                .correo("sur@mail.com")
                .telefono("123456789")
                .build();

        when(proveedorService.guardarProveedor(any(Proveedor.class))).thenReturn(proveedor);

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Distribuidora Sur"));
    }

    @Test
    void obtenerProveedorPorId() throws Exception {
        Proveedor proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Distribuidora Sur")
                .correo("sur@mail.com")
                .telefono("123456789")
                .build();

        when(proveedorService.obtenerProveedorPorId(1L)).thenReturn(proveedor);

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("sur@mail.com"));
    }

    @Test
    void listarProveedores() throws Exception {
        Proveedor p1 = Proveedor.builder().id(1L).nombre("Sur").correo("sur@mail.com").telefono("123").build();
        Proveedor p2 = Proveedor.builder().id(2L).nombre("Norte").correo("norte@mail.com").telefono("456").build();

        when(proveedorService.obtenerTodos()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void actualizarProveedor() throws Exception {
        Proveedor actualizado = Proveedor.builder()
                .id(1L)
                .nombre("Sur Express")
                .correo("sur@mail.com")
                .telefono("987654321")
                .build();

        when(proveedorService.actualizarProveedor(eq(1L), any(Proveedor.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sur Express"));
    }

    @Test
    void eliminarProveedor() throws Exception {
        doNothing().when(proveedorService).eliminarProveedor(1L);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isNoContent());
    }
}
