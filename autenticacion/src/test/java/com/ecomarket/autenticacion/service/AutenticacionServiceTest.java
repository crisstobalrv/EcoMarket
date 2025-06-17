package com.ecomarket.autenticacion.service;

import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticacionServiceTest {

    private AutenticacionRepository autenticacionRepository;
    private AutenticacionService autenticacionService;

    @BeforeEach
    void setUp() {
        autenticacionRepository = mock(AutenticacionRepository.class);
        autenticacionService = new AutenticacionService(autenticacionRepository);
    }

    @Test
    void registrar_usuarioValido_deberiaGuardar() {
        Usuario nuevo = new Usuario(null, "Juan", "juan@example.com", "1234", "cliente");

        when(autenticacionRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(autenticacionRepository.save(nuevo)).thenReturn(nuevo);

        Usuario resultado = autenticacionService.registrar(nuevo);

        assertEquals("Juan", resultado.getNombre());
        verify(autenticacionRepository).save(nuevo);
    }

    @Test
    void registrar_emailYaRegistrado_deberiaLanzarExcepcion() {
        Usuario existente = new Usuario(1L, "Ana", "ana@example.com", "pass", "admin");
        when(autenticacionRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(existente));

        Usuario nuevo = new Usuario(null, "Ana", "ana@example.com", "pass", "admin");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            autenticacionService.registrar(nuevo);
        });

        assertTrue(ex.getMessage().contains("correo ya está registrado"));
    }

    @Test
    void login_credencialesCorrectas_deberiaRetornarTrue() {
        Usuario user = new Usuario(1L, "Pedro", "pedro@mail.com", "abcd", "cliente");
        when(autenticacionRepository.findByEmail("pedro@mail.com")).thenReturn(Optional.of(user));

        boolean resultado = autenticacionService.login("pedro@mail.com", "abcd");

        assertTrue(resultado);
    }

    @Test
    void login_contrasenaIncorrecta_deberiaRetornarFalse() {
        Usuario user = new Usuario(1L, "Pedro", "pedro@mail.com", "abcd", "cliente");
        when(autenticacionRepository.findByEmail("pedro@mail.com")).thenReturn(Optional.of(user));

        boolean resultado = autenticacionService.login("pedro@mail.com", "xyz");

        assertFalse(resultado);
    }

    @Test
    void obtenerUsuario_existente_deberiaRetornarUsuario() {
        Usuario user = new Usuario(1L, "Luisa", "luisa@mail.com", "123", "cliente");
        when(autenticacionRepository.findByEmail("luisa@mail.com")).thenReturn(Optional.of(user));

        Usuario resultado = autenticacionService.obtenerUsuario("luisa@mail.com");

        assertEquals("Luisa", resultado.getNombre());
    }

    @Test
    void obtenerUsuario_inexistente_deberiaLanzarExcepcion() {
        when(autenticacionRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> autenticacionService.obtenerUsuario("noexiste@mail.com"));
    }
}
