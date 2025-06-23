package com.ecomarket.clientes.service;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        restTemplate = mock(RestTemplate.class);
        clienteService = new ClienteService(clienteRepository, restTemplate);
    }

    @Test
    void guardarCliente() {
        Cliente cliente = Cliente.builder()
                .nombre("Luis")
                .apellido("Gómez")
                .rut("12345678-9")
                .correo("luis@mail.com")
                .telefono("123456789")
                .direccion("Dirección X")
                .build();

        // Mock: simulamos que el correo existe en el MS de Autenticación
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);

        // Mock: simulamos que no está registrado aún en Clientes
        when(clienteRepository.findByCorreo(cliente.getCorreo())).thenReturn(Optional.empty());
        when(clienteRepository.findByRut(cliente.getRut())).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.registrar(cliente);

        assertEquals("Luis", resultado.getNombre());
        assertEquals("luis@mail.com", resultado.getCorreo());
    }



    @Test
    void actualizarCliente() {
        Cliente existente = Cliente.builder()
                .id(1L)
                .nombre("Luis")
                .apellido("Gómez")
                .correo("luis@mail.com")
                .rut("12345678-9")
                .telefono("123")
                .direccion("Avenida 1")
                .build();

        Cliente modificado = Cliente.builder()
                .nombre("Luis Enrique") // cambio permitido
                .apellido("Gómez")
                .correo("luis@mail.com") // igual al original
                .rut("12345678-9")
                .telefono("456")
                .direccion("Nueva Dirección")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(modificado);

        Cliente actualizado = clienteService.actualizar(1L, modificado);

        assertEquals("Luis Enrique", actualizado.getNombre());
        assertEquals("456", actualizado.getTelefono());
    }



    @Test
    void actualizarCliente_modificandoCorreo_lanzaExcepcion() {
        Cliente existente = Cliente.builder()
                .id(1L)
                .nombre("Luis")
                .apellido("Gómez")
                .correo("luis@mail.com")
                .rut("12345678-9")
                .telefono("123")
                .direccion("Avenida 1")
                .build();

        Cliente modificado = Cliente.builder()
                .nombre("Luis")
                .apellido("Gómez")
                .correo("otro@mail.com")
                .rut("12345678-9")
                .telefono("123")
                .direccion("Avenida 1")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));

        assertThrows(RuntimeException.class, () ->
                clienteService.actualizar(1L, modificado)
        );
    }



    @Test
    void obtenerCliente() {
        Cliente cliente = Cliente.builder()
                .id(2L)
                .nombre("Ana")
                .apellido("Ramírez")
                .rut("11222333-4")
                .correo("ana@mail.com")
                .telefono("912345678")
                .direccion("Calle Falsa 456")
                .build();

        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.obtenerPorId(2L);

        assertEquals("Ana", resultado.getNombre());
        assertEquals("ana@mail.com", resultado.getCorreo());
    }

    @Test
    void listarClientes() {
        List<Cliente> lista = Arrays.asList(
                Cliente.builder().id(1L).nombre("Juan").correo("juan@mail.com").rut("11111111-1").build(),
                Cliente.builder().id(2L).nombre("Maria").correo("maria@mail.com").rut("22222222-2").build()
        );

        when(clienteRepository.findAll()).thenReturn(lista);

        List<Cliente> resultado = clienteService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Maria", resultado.get(1).getNombre());
    }

    @Test
    void eliminarCliente() {
        Long id = 5L;
        doNothing().when(clienteRepository).deleteById(id);

        clienteService.eliminar(id);

        verify(clienteRepository).deleteById(id);
    }
}
