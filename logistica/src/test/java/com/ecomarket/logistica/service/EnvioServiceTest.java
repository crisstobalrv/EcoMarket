package com.ecomarket.logistica.service;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.repository.EnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnvioService envioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearEnvioExitoso() {
        Envio envio = new Envio();
        envio.setVentaId(1L);

        when(envioRepo.findByVentaId(1L)).thenReturn(List.of());
        when(restTemplate.getForEntity("http://localhost:8086/api/ventas/1", String.class))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));
        when(envioRepo.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio creado = envioService.crearEnvio(envio);

        assertEquals("En preparación", creado.getEstado());
        assertNotNull(creado.getFechaEnvio());
        verify(envioRepo, times(1)).save(envio);
    }

    @Test
    void CrearEnvio() {
        Envio envio = new Envio();
        envio.setVentaId(1L);

        when(envioRepo.findByVentaId(1L)).thenReturn(List.of(new Envio()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> envioService.crearEnvio(envio));
        assertEquals("Ya existe un envío registrado para esta venta.", ex.getMessage());
    }

    @Test
    void CrearEnvioVentaNoExiste() {
        Envio envio = new Envio();
        envio.setVentaId(2L);

        when(envioRepo.findByVentaId(2L)).thenReturn(List.of());
        when(restTemplate.getForEntity("http://localhost:8086/api/ventas/2", String.class))
                .thenThrow(new RuntimeException("Venta no encontrada"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> envioService.crearEnvio(envio));
        assertEquals("No se puede crear el envío: la venta no existe.", ex.getMessage());
    }

    @Test
    void testActualizar() {
        Envio envio = new Envio();
        envio.setId(1L);
        envio.setEstado("En preparación");

        when(envioRepo.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepo.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

        Envio actualizado = envioService.actualizarEstado(1L, "En camino");

        assertEquals("En camino", actualizado.getEstado());
        verify(envioRepo, times(1)).save(envio);
    }

    @Test
    void testActualizarEstadoEnvioNoExiste() {
        when(envioRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> envioService.actualizarEstado(99L, "En camino"));
        assertEquals("Envío no encontrado", ex.getMessage());
    }

    @Test
    void testObtenerTodos() {
        List<Envio> lista = List.of(new Envio(), new Envio());
        when(envioRepo.findAll()).thenReturn(lista);

        List<Envio> resultado = envioService.obtenerTodos();
        assertEquals(2, resultado.size());
    }

    @Test
    void testObtenerPorId() {
        Envio envio = new Envio();
        envio.setId(10L);
        when(envioRepo.findById(10L)).thenReturn(Optional.of(envio));

        Optional<Envio> resultado = envioService.obtenerPorId(10L);
        assertTrue(resultado.isPresent());
    }


}
