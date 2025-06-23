package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.model.DetallePedido;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.external.Producto;
import com.ecomarket.pedidos.repository.DetallePedidoRepository;
import com.ecomarket.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Pedido crearPedidoBase() {
        Pedido pedido = new Pedido();
        pedido.setClienteId(1L);
        pedido.setEstado("En preparación");
        pedido.setTotal(5000.0);

        DetallePedido detalle = new DetallePedido();
        detalle.setProductoId(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(2500.0);
        pedido.setDetalles(List.of(detalle));

        return pedido;
    }

    @Test
    void testCrearPedido() {
        Pedido pedido = crearPedidoBase();

        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Test");
        producto.setDescripcion("desc");
        producto.setPrecio(2500.);
        producto.setCategoria("otros");

        // Mock para disponibilidad
        when(restTemplate.getForObject("http://localhost:8083/api/productos/10/disponibilidad/2", Boolean.class))
                .thenReturn(true);

        // Mock para producto real
        when(restTemplate.getForObject("http://localhost:8083/api/productos/10", Producto.class))
                .thenReturn(producto);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        Pedido creado = pedidoService.registrar(pedido);

        assertEquals(1L, creado.getClienteId());
        assertEquals(1, creado.getDetalles().size());
        assertEquals(5000, creado.getTotal()); // 2500 * 2
    }

    @Test
    void testObtenerTodos() {
        Pedido pedido = crearPedidoBase();
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<Pedido> pedidos = pedidoService.listarTodos();

        assertEquals(1, pedidos.size());
    }

    @Test
    void testObtenerPorId() {
        Pedido pedido = crearPedidoBase();
        pedido.setId(5L);

        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.buscarPorId(5L);
        assertTrue(resultado.isPresent());
        assertEquals(5L, resultado.get().getId());
    }

    @Test
    void testActualizarEstado() {
        Pedido pedido = crearPedidoBase();
        pedido.setId(8L);
        when(pedidoRepository.findById(8L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pedido actualizado = pedidoService.actualizarEstado(8L, "Entregado");

        assertEquals("Entregado", actualizado.getEstado());
    }

    @Test
    void testEliminar() {
        Long pedidoId = 7L;

        doNothing().when(pedidoRepository).deleteById(pedidoId);

        pedidoService.eliminarPorId(pedidoId);

        verify(pedidoRepository, times(1)).deleteById(pedidoId);
    }

    @Test
    void testBuscarPorCliente() {
        Pedido pedido = crearPedidoBase();
        pedido.setClienteId(123L);

        when(pedidoRepository.findByClienteId(123L)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.buscarPorCliente(123L);

        assertEquals(1, resultado.size());
        assertEquals(123L, resultado.get(0).getClienteId());
    }


}
