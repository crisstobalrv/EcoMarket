package com.ecomarket.inventario.service;

import com.ecomarket.inventario.dto.ProductoCreateRequest;
import com.ecomarket.inventario.dto.ProductoUpdateRequest;
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

        ProductoCreateRequest request = ProductoCreateRequest.builder()
                .nombre("Tomate")
                .descripcion("Tomate fresco")
                .categoria("Verdura")
                .stock(50)
                .precio(3000.0)
                .proveedorId(10L)
                .build();


        Producto guardado = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .descripcion("Tomate fresco")
                .categoria("Verdura")
                .stock(50)
                .precio(3000.0)
                .proveedor(proveedorMock)
                .build();


        when(proveedorRepository.findById(10L)).thenReturn(Optional.of(proveedorMock));
        when(productoRepository.findByNombreAndProveedorId("Tomate", 10L)).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);


        Producto resultado = productoService.guardarProducto(request);


        assertNotNull(resultado);
        assertEquals("Tomate", resultado.getNombre());
        assertEquals(3000.0, resultado.getPrecio());
        verify(productoRepository).save(any(Producto.class));
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
        // Datos actuales en la base
        Producto productoExistente = Producto.builder()
                .id(1L)
                .nombre("Tomate")
                .descripcion("Antigua")
                .categoria("Verdura")
                .precio(3000.0)
                .stock(50)
                .proveedor(Proveedor.builder().id(1L).build())
                .build();

        // Datos que el cliente envía en la petición (DTO)
        ProductoUpdateRequest actualizado = ProductoUpdateRequest.builder()
                .nombre("Tomate Cherry")
                .descripcion("Rojo y dulce")
                .categoria("Verdura")
                .stock(60)
                .precio(3200.0)
                .proveedorId(1L)
                .build();

        // Proveedor simulado
        Proveedor proveedor = Proveedor.builder()
                .id(1L)
                .nombre("Proveedor Uno")
                .correo("proveedor@uno.cl")
                .telefono("123456789")
                .rut("12.345.678-9")
                .build();

        // Producto final después de guardar
        Producto productoActualizado = Producto.builder()
                .id(1L)
                .nombre("Tomate Cherry")
                .descripcion("Rojo y dulce")
                .categoria("Verdura")
                .stock(60)
                .precio(3200.0)
                .proveedor(proveedor)
                .build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        Producto resultado = productoService.actualizarProducto(1L, actualizado);

        assertEquals("Tomate Cherry", resultado.getNombre());
        assertEquals(60, resultado.getStock());
        assertEquals("Rojo y dulce", resultado.getDescripcion());
        assertEquals(3200.0, resultado.getPrecio());
        assertEquals("Proveedor Uno", resultado.getProveedor().getNombre());
    }


    @Test
    void eliminarProducto() {
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminarProducto(1L);

        verify(productoRepository).deleteById(1L);
    }
}
