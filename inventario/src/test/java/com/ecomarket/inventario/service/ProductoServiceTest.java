package com.ecomarket.inventario.service;

import com.ecomarket.inventario.model.Producto;
import com.ecomarket.inventario.model.Proveedor;
import com.ecomarket.inventario.repository.ProductoRepository;
import com.ecomarket.inventario.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductoServiceTest {

    private ProductoRepository productoRepository;
    private ProveedorRepository proveedorRepository;
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoRepository = mock(ProductoRepository.class);
        proveedorRepository = mock(ProveedorRepository.class);
        productoService = new ProductoService(productoRepository, proveedorRepository);
    }


    @Test
    void guardarProducto() {
        Proveedor proveedorMock = Proveedor.builder()
                .id(10L)
                .nombre("Proveedor X")
                .correo("proveedor@mail.com")
                .build();

        Producto nuevo = Producto.builder()
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .proveedor(proveedorMock)
                .build();

        Producto guardado = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .proveedor(proveedorMock)
                .build();


        when(proveedorRepository.findById(10L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.save(nuevo)).thenReturn(guardado);


        Producto resultado = productoService.guardarProducto(nuevo);

        assertNotNull(resultado);
        assertEquals("Tomate", resultado.getNombre());
        verify(productoRepository).save(nuevo);
    }

    @Test
    void obtenerProductoporId() {
        Producto producto = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerProductoPorId(1L);  // ← Aquí

        assertEquals("Tomate", resultado.getNombre());
        assertEquals(50, resultado.getStock());
    }



    @Test
    void listarProductos() {
        List<Producto> lista = Arrays.asList(
                Producto.builder().id(1L).nombre("Tomate").categoria("Verdura").precio(3000.0).stock(50).build(),
                Producto.builder().id(2L).nombre("Leche").categoria("Lácteos").precio(1500.0).stock(20).build()
        );

        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    void actualizarProducto() {
        Producto existente = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .build();

        Producto actualizado = Producto.builder()
                .nombre("Tomate Cherry")
                .categoria("Verdura")
                .precio(3500.0)
                .stock(60)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        Producto resultado = productoService.actualizarProducto(1L, actualizado);

        assertEquals("Tomate Cherry", resultado.getNombre());
        assertEquals(60, resultado.getStock());
    }

    @Test
    void eliminarProducto() {
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }
}
