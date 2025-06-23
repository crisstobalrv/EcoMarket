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
    void registrar_usuario() {
        Usuario nuevo = new Usuario(null, "Juan", "juan@example.com", "1234", "cliente");

        when(autenticacionRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(autenticacionRepository.save(nuevo)).thenReturn(nuevo);

        Usuario resultado = autenticacionService.registrar(nuevo);

        assertEquals("Juan", resultado.getNombre());
        verify(autenticacionRepository).save(nuevo);
    }

    @Test
    void registrar_emailYaRegistrado() {
        Usuario existente = new Usuario(1L, "Ana", "ana@example.com", "pass", "admin");
        when(autenticacionRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(existente));

        Usuario nuevo = new Usuario(null, "Ana", "ana@example.com", "pass", "admin");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            autenticacionService.registrar(nuevo);
        });

        assertTrue(ex.getMessage().contains("Correo ya está registrado"));
    }

    @Test
    void logearse() {
        Usuario user = new Usuario(1L, "Pedro", "pedro@mail.com", "abcd", "cliente");
        when(autenticacionRepository.findByEmail("pedro@mail.com")).thenReturn(Optional.of(user));

        boolean resultado = autenticacionService.login("pedro@mail.com", "abcd");

        assertTrue(resultado);
    }

    @Test
    void login_contrasenaIncorrecta() {
        Usuario user = new Usuario(1L, "Pedro", "pedro@mail.com", "abcd", "cliente");
        when(autenticacionRepository.findByEmail("pedro@mail.com")).thenReturn(Optional.of(user));

        boolean resultado = autenticacionService.login("pedro@mail.com", "xyz");

        assertFalse(resultado);
    }

}
