package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProveedorServiceTest {

    private ProveedorRepository proveedorRepository;
    private ProveedorService proveedorService;

    @BeforeEach
    void setUp() {
        proveedorRepository = mock(ProveedorRepository.class);
        proveedorService = new ProveedorService(proveedorRepository);
    }

    @Test
    void guardarProveedor_deberiaGuardarYRetornar() {
        Proveedor nuevo = Proveedor.builder()
                .nombre("Proveedor A")
                .correo("proveedora@mail.com")
                .telefono("123456789")
                .build();

        Proveedor guardado = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor A")
                .correo("proveedora@mail.com")
                .telefono("123456789")
                .build();

        when(proveedorRepository.save(nuevo)).thenReturn(guardado);

        Proveedor resultado = proveedorService.guardarProveedor(nuevo);

        assertEquals("Proveedor A", resultado.getNombre());
        assertEquals("proveedora@mail.com", resultado.getCorreo());
    }

    @Test
    void obtenerProveedorPorId_existente_deberiaRetornarProveedor() {
        Proveedor proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor B")
                .correo("b@mail.com")
                .telefono("987654321")
                .build();

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        Proveedor resultado = proveedorService.obtenerProveedorPorId(1L);

        assertEquals("Proveedor B", resultado.getNombre());
    }

    @Test
    void obtenerProveedorPorId_noExistente_deberiaLanzarExcepcion() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            proveedorService.obtenerProveedorPorId(99L);
        });

        assertTrue(exception.getMessage().contains("Proveedor no encontrado"));
    }

    @Test
    void obtenerTodos_deberiaRetornarLista() {
        List<Proveedor> lista = Arrays.asList(
                Proveedor.builder().id(1L).nombre("X").correo("x@mail.com").telefono("111").build(),
                Proveedor.builder().id(2L).nombre("Y").correo("y@mail.com").telefono("222").build()
        );

        when(proveedorRepository.findAll()).thenReturn(lista);

        List<Proveedor> resultado = proveedorService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("X", resultado.get(0).getNombre());
    }

    @Test
    void actualizarProveedor_deberiaModificarYRetornar() {
        Proveedor existente = Proveedor.builder()
                .id(1L)
                .nombre("Antiguo")
                .correo("a@mail.com")
                .telefono("111")
                .build();

        Proveedor cambios = Proveedor.builder()
                .nombre("Nuevo")
                .correo("a@mail.com")
                .telefono("999")
                .build();

        Proveedor actualizado = Proveedor.builder()
                .id(1L)
                .nombre("Nuevo")
                .correo("a@mail.com")
                .telefono("999")
                .build();

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(actualizado);

        Proveedor resultado = proveedorService.actualizarProveedor(1L, cambios);

        assertEquals("Nuevo", resultado.getNombre());
        assertEquals("999", resultado.getTelefono());
    }

    @Test
    void eliminarProveedor_deberiaEjecutarDelete() {
        doNothing().when(proveedorRepository).deleteById(5L);

        proveedorService.eliminarProveedor(5L);

        verify(proveedorRepository).deleteById(5L);
    }
}
