package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.dto.DetallePedidoDTO;
import com.ecomarket.pedidos.dto.PedidoRequestDTO;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        pedido = crearPedidoMock();
    }

    @Test
    void crearPedido_deberiaRetornarPedidoCreado() throws Exception {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(123L);
        dto.setDetalles(List.of(new DetallePedidoDTO(10L, 2)));

        when(pedidoService.registrarDesdeDto(any())).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(123));
    }

    @Test
    void listarPedidos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.pedidoList[0].id").value(1));
    }

    @Test
    void obtenerPedidoPorId() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarEstadoPedido() throws Exception {
        when(pedidoService.actualizarEstado(1L, "En camino")).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("estado", "En camino"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Pendiente")); // Cambia si tu método realmente actualiza el estado en `pedido`
    }

    @Test
    void eliminarPedidoPorId() throws Exception {
        doNothing().when(pedidoService).eliminarPorId(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El pedido fue eliminado correctamente."))
                .andExpect(jsonPath("$.pedidoId").value(1));

        verify(pedidoService, times(1)).eliminarPorId(1L);
    }

    @Test
    void obtenerPedidosPorCliente() throws Exception {
        when(pedidoService.buscarPorCliente(123L)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos/cliente/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.pedidoList[0].clienteId").value(123));

    }

    // Helper para construir un Pedido
    private Pedido crearPedidoMock() {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setClienteId(123L);
        p.setEstado("Pendiente");
        p.setFecha(LocalDate.now());
        p.setTotal(5000.0);
        return p;
    }
}
