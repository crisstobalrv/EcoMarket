package com.ecomarket.inventario.controller;

import com.ecomarket.inventario.dto.ProductoCreateRequest;
import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarProducto() throws Exception {

        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .build();

        when(productoService.guardarProducto(any(ProductoCreateRequest.class))).thenReturn(producto);


        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Tomate"));
    }

    @Test
    void obtenerProductoPorId() throws Exception {
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .build();

        when(productoService.obtenerProductoPorId(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Tomate"));
    }

    @Test
    void listarProductos() throws Exception {
        Producto producto1 = Producto.builder()
                .id(1L).nombre("Tomate").categoria("Verdura").precio(3000.0).stock(50).build();
        Producto producto2 = Producto.builder()
                .id(2L).nombre("Leche").categoria("Lácteos").precio(1500.0).stock(20).build();

        when(productoService.obtenerTodos()).thenReturn(Arrays.asList(producto1, producto2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList.length()").value(2));
    }

    @Test
    void actualizarProducto() throws Exception {
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Tomate Cherry")
                .categoria("Verdura")
                .precio(3500.0)
                .stock(60)
                .build();

        when(productoService.actualizarProducto(eq(1L), any(com.ecomarket.inventario.dto.ProductoUpdateRequest.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre").value("Tomate Cherry"));
    }

    @Test
    void eliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
