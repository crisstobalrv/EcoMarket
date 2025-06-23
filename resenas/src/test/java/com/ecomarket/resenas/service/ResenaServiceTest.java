package com.ecomarket.resenas.service;

import com.ecomarket.resenas.dto.CrearResenaDTO;
import com.ecomarket.resenas.dto.EditarResenaDTO;
import com.ecomarket.resenas.external.DetallePedido;
import com.ecomarket.resenas.external.EmbeddedPedidos;
import com.ecomarket.resenas.external.Pedido;
import com.ecomarket.resenas.external.PedidoResponse;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
import jakarta.persistence.Embedded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ResenaService service;

    private Resena resena;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resena = Resena.builder()
                .id(1L)
                .clienteId(1L)
                .productoId(10L)
                .comentario("Muy bueno")
                .puntuacion(5)
                .build();
    }

    @Test
    void testCrearResena() {
        Pedido pedido = new Pedido();
        pedido.setDetalles(List.of(new DetallePedido(10L, 1)));

        EmbeddedPedidos embedded = new EmbeddedPedidos();
        embedded.setPedidoList(List.of(pedido));

        PedidoResponse response = new PedidoResponse();
        response.set_embedded(embedded);

        when(restTemplate.getForObject(contains("/productos/"), eq(Void.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/clientes/"), eq(Void.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/pedidos/cliente/"), eq(PedidoResponse.class))).thenReturn(response);
        when(resenaRepo.findByClienteIdAndProductoId(123L, 10L)).thenReturn(Optional.empty());
        when(resenaRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CrearResenaDTO dto = new CrearResenaDTO();
        dto.setProductoId(10L);
        dto.setClienteId(123L);
        dto.setComentario("Muy bueno");
        dto.setPuntuacion(5);

        Resena creada = service.crearDesdeDTO(dto);

        assertEquals("Muy bueno", creada.getComentario());
        verify(resenaRepo, times(1)).save(any());
    }





    @Test
    void testCrearResenaSinCompra() {
        when(restTemplate.getForObject(contains("/productos/"), eq(Void.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/clientes/"), eq(Void.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("/pedidos/cliente/"), eq(Pedido[].class))).thenReturn(new Pedido[0]);

        CrearResenaDTO dto = new CrearResenaDTO();
        dto.setProductoId(10L);
        dto.setClienteId(123L);
        dto.setComentario("Muy bueno");
        dto.setPuntuacion(5);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.crearDesdeDTO(dto));
        assertEquals("El cliente no ha comprado este producto.", ex.getMessage());
    }


    @Test
    void testEditarResena() {
        Resena existente = new Resena();
        existente.setId(1L);
        existente.setComentario("Antiguo");
        existente.setPuntuacion(2);

        when(resenaRepo.findById(1L)).thenReturn(Optional.of(existente));

        EditarResenaDTO dto = new EditarResenaDTO();
        dto.setComentario("Modificado");
        dto.setPuntuacion(3);

        service.editarDesdeDTO(1L, dto);

        verify(resenaRepo).save(argThat(r ->
                r.getComentario().equals("Modificado") &&
                        r.getPuntuacion() == 3 &&
                        r.getFecha() != null));
    }


    @Test
    void testEliminar() {
        service.eliminar(1L);
        verify(resenaRepo).deleteById(1L);
    }

    @Test
    void testListarTodas() {
        when(resenaRepo.findAll()).thenReturn(List.of(resena));
        List<Resena> resultado = service.listarTodas();
        assertEquals(1, resultado.size());
    }

    @Test
    void testListarPorProducto() {
        when(resenaRepo.findByProductoId(10L)).thenReturn(List.of(resena));
        List<Resena> resultado = service.listarPorProducto(10L);
        assertEquals(1, resultado.size());
    }

    @Test
    void testListarPorCliente() {
        when(resenaRepo.findByClienteId(1L)).thenReturn(List.of(resena));
        List<Resena> resultado = service.listarPorCliente(1L);
        assertEquals(1, resultado.size());
    }
}
