package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.dto.DetallePedidoDTO;
import com.ecomarket.pedidos.dto.PedidoRequestDTO;
import com.ecomarket.pedidos.model.DetallePedido;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setClienteId(123L);
        pedido.setEstado("Pendiente");
        pedido.setFecha(LocalDate.now());
        pedido.setTotal(5000.0);
    }

    @Test
    void testCrearPedido() throws Exception {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(123L);

        DetallePedidoDTO detalle = new DetallePedidoDTO();
        detalle.setProductoId(10L);
        detalle.setCantidad(2);

        dto.setDetalles(List.of(detalle));

        when(pedidoService.registrarDesdeDto(any())).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(123));
    }


    @Test
    void testListarTodos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.pedidoList[0].id").value(1));
    }


    @Test
    void testObtenerporId() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testActualizarEstado() throws Exception {
        pedido.setEstado("En camino");
        when(pedidoService.actualizarEstado(1L, "En camino")).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "En camino"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("En camino"));
    }

    @Test
    void testEliminarPorId() throws Exception {
        doNothing().when(pedidoService).eliminarPorId(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El pedido fue eliminado correctamente."))
                .andExpect(jsonPath("$.pedidoId").value(1));
    }

    @Test
    void testObtenerPorCliente() throws Exception {
        when(pedidoService.buscarPorCliente(123L)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos/cliente/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.pedidoList[0].clienteId").value(123));
    }

}
