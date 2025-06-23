package com.ecomarket.ventas.service;

import com.ecomarket.ventas.external.DetallePedido;
import com.ecomarket.ventas.external.Pedido;
import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaServiceTest {

    private VentaRepository ventaRepo;
    private RestTemplate restTemplate;
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        ventaRepo = mock(VentaRepository.class);
        restTemplate = mock(RestTemplate.class);
        ventaService = new VentaService(ventaRepo, restTemplate);
    }

    @Test
    void testRegistrarVenta() {
        Venta venta = new Venta();
        venta.setPedidoId(1L);
        venta.setMedioPago("efectivo");

        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setClienteId(10L);
        pedido.setTotal(5000.0);

        DetallePedido detalle = new DetallePedido();
        detalle.setProductoId(5L);
        detalle.setCantidad(2);

        pedido.setDetalles(List.of(detalle));


        when(ventaRepo.findByPedidoId(1L)).thenReturn(Collections.emptyList());
        when(restTemplate.getForObject(contains("/api/pedidos/1"), eq(Pedido.class))).thenReturn(pedido);
        when(ventaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Venta registrada = ventaService.registrarVentaDesdeDTO(venta);

        assertEquals("Pagada", registrada.getEstado());
        assertEquals(5000.0, registrada.getTotalVenta());
        assertEquals(10L, registrada.getClienteId());
        assertEquals(LocalDate.now(), registrada.getFechaVenta());

        verify(restTemplate).put(contains("/estado"), any());
        verify(restTemplate).put(contains("/descontar/2"), isNull());
        verify(ventaRepo).save(any());
    }

    @Test
    void testGenerarFactura() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setFechaVenta(LocalDate.of(2025, 6, 23));
        venta.setClienteId(100L);
        venta.setPedidoId(200L);
        venta.setMedioPago("efectivo");
        venta.setTotalVenta(3000.0);

        when(ventaRepo.findById(1L)).thenReturn(Optional.of(venta));

        Map<String, Object> factura = ventaService.generarFactura(1L);

        assertEquals("F-1", factura.get("numeroFactura"));
        assertEquals(3000.0, factura.get("total"));
    }

    @Test
    void testAnularVenta() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEstado("Pagada");

        when(ventaRepo.findById(1L)).thenReturn(Optional.of(venta));

        boolean resultado = ventaService.anularVenta(1L);

        assertTrue(resultado);
        verify(ventaRepo).save(any());
    }

    @Test
    void testObtenerTodas() {
        List<Venta> ventas = List.of(new Venta(), new Venta());
        when(ventaRepo.findAll()).thenReturn(ventas);

        List<Venta> resultado = ventaService.obtenerTodas();

        assertEquals(2, resultado.size());
        verify(ventaRepo, times(1)).findAll();
    }

    @Test
    void testObtenerPorCliente() {
        Long clienteId = 42L;
        List<Venta> ventas = List.of(new Venta(), new Venta());
        when(ventaRepo.findByClienteId(clienteId)).thenReturn(ventas);

        List<Venta> resultado = ventaService.obtenerPorCliente(clienteId);

        assertEquals(2, resultado.size());
        verify(ventaRepo).findByClienteId(clienteId);
    }

    @Test
    void testBuscarPorId() {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaRepo.findById(1L)).thenReturn(Optional.of(venta));

        Optional<Venta> resultado = ventaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }




}
