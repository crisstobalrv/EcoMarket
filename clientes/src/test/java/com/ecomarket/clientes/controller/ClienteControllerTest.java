package com.ecomarket.clientes.controller;

import com.ecomarket.clientes.model.Cliente;
import com.ecomarket.clientes.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarCliente_deberiaRetornar201() throws Exception {
        Cliente cliente = Cliente.builder()
                .nombre("Luis")
                .apellido("Gómez")
                .rut("12345678-9")
                .correo("luis@mail.com")
                .telefono("987654321")
                .direccion("Av. Siempre Viva 123")
                .build();

        when(clienteService.registrar(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/clientes/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Luis"))
                .andExpect(jsonPath("$.correo").value("luis@mail.com"));
    }

    @Test
    void obtenerClientePorId_deberiaRetornar200() throws Exception {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Ramírez")
                .rut("11222333-4")
                .correo("ana@mail.com")
                .telefono("912345678")
                .direccion("Calle Falsa 456")
                .build();

        when(clienteService.obtenerPorId(1L)).thenReturn(cliente);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.rut").value("11222333-4"));
    }

    @Test
    void listarClientes_deberiaRetornarLista() throws Exception {
        List<Cliente> lista = Arrays.asList(
                Cliente.builder().id(1L).nombre("Juan").rut("11111111-1").correo("juan@mail.com").build(),
                Cliente.builder().id(2L).nombre("Maria").rut("22222222-2").correo("maria@mail.com").build()
        );

        when(clienteService.obtenerTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Maria"));
    }

    @Test
    void actualizarCliente_deberiaRetornarClienteActualizado() throws Exception {
        Cliente actualizado = Cliente.builder()
                .id(1L)
                .nombre("Luis Miguel")
                .apellido("Gómez")
                .rut("12345678-9")
                .correo("luismiguel@mail.com")
                .telefono("999999999")
                .direccion("Nueva dirección")
                .build();

        when(clienteService.actualizar(eq(1L), any(Cliente.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luis Miguel"))
                .andExpect(jsonPath("$.correo").value("luismiguel@mail.com"));
    }

    @Test
    void eliminarCliente_deberiaRetornar204() throws Exception {
        doNothing().when(clienteService).eliminar(1L);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminar(1L);
    }
}
