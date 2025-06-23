package com.ecomarket.reportes.service;

import com.ecomarket.reportes.external.DetallePedido;
import com.ecomarket.reportes.external.Pedido;
import com.ecomarket.reportes.external.Producto;
import com.ecomarket.reportes.external.Venta;
import com.ecomarket.reportes.model.Reporte;
import com.ecomarket.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteServiceTest {

    @Mock
    private ReporteRepository repo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReporteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarReporte() {
        Reporte mock = Reporte.builder()
                .id(1L)
                .tipo("Test")
                .fechaGeneracion(LocalDate.now())
                .datos("datos")
                .build();

        when(repo.save(any())).thenReturn(mock);

        Reporte resultado = service.guardarReporte("Test", "datos");

        assertEquals("Test", resultado.getTipo());
        assertEquals("datos", resultado.getDatos());
    }

    @Test
    void testGenerarReporteVentasPorFecha() {
        Venta venta1 = new Venta();
        venta1.setId(1L);
        venta1.setFecha(LocalDate.now());
        venta1.setTotalVenta(5000.0);

        Venta venta2 = new Venta();
        venta2.setId(2L);
        venta2.setFecha(LocalDate.now());
        venta2.setTotalVenta(10000.0);

        List<Venta> ventas = List.of(venta1, venta2);

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Reporte reporte = service.generarReporteVentasPorFecha(LocalDate.now().minusDays(1), LocalDate.now(), ventas);

        assertEquals("Ventas por fecha", reporte.getTipo());
        assertTrue(reporte.getDatos().contains("totalVendido"));
    }


    @Test
    void testGenerarReportePedidosPorEstado() {
        Pedido p1 = new Pedido();
        p1.setId(1L);
        p1.setEstado("Pendiente");
        p1.setDetalles(List.of());

        Pedido p2 = new Pedido();
        p2.setId(2L);
        p2.setEstado("Entregado");
        p2.setDetalles(List.of());

        Pedido p3 = new Pedido();
        p3.setId(3L);
        p3.setEstado("Pendiente");
        p3.setDetalles(List.of());

        List<Pedido> pedidos = List.of(p1, p2, p3);

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Reporte reporte = service.generarReportePedidosPorEstado(pedidos);

        assertEquals("Pedidos por estado", reporte.getTipo());
        assertTrue(reporte.getDatos().contains("Pendiente"));
    }



    @Test
    void testGenerarReporteProductosMasVendidos() {
        DetallePedido d1 = new DetallePedido();
        d1.setProductoId(10L);
        d1.setCantidad(3);

        DetallePedido d2 = new DetallePedido();
        d2.setProductoId(10L);
        d2.setCantidad(2);

        DetallePedido d3 = new DetallePedido();
        d3.setProductoId(20L);
        d3.setCantidad(5);

        Pedido p1 = new Pedido();
        p1.setId(1L);
        p1.setEstado("Entregado");
        p1.setDetalles(List.of(d1, d2));

        Pedido p2 = new Pedido();
        p2.setId(2L);
        p2.setEstado("Entregado");
        p2.setDetalles(List.of(d3));

        List<Pedido> pedidos = List.of(p1, p2);

        Producto prod10 = new Producto();
        prod10.setId(10L);
        prod10.setNombre("Manzana");

        Producto prod20 = new Producto();
        prod20.setId(20L);
        prod20.setNombre("Pera");

        when(restTemplate.getForObject(contains("/10"), eq(Producto.class))).thenReturn(prod10);
        when(restTemplate.getForObject(contains("/20"), eq(Producto.class))).thenReturn(prod20);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Reporte reporte = service.generarReporteProductosMasVendidos(pedidos);

        assertEquals("Productos más vendidos", reporte.getTipo());
        assertTrue(reporte.getDatos().contains("Manzana"));
        assertTrue(reporte.getDatos().contains("Pera"));
    }


    @Test
    void testListarTodos() {
        when(repo.findAll()).thenReturn(List.of(new Reporte(), new Reporte()));
        List<Reporte> lista = service.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    void testBuscarPorIdExistente() {
        Reporte reporte = Reporte.builder().id(5L).build();
        when(repo.findById(5L)).thenReturn(Optional.of(reporte));

        Reporte encontrado = service.buscarPorId(5L);
        assertEquals(5L, encontrado.getId());
    }

    @Test
    void testBuscarPorIdInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
        assertEquals("Reporte no encontrado", ex.getMessage());
    }
}
