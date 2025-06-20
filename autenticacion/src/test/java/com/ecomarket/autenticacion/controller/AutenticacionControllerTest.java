package com.ecomarket.autenticacion.controller;

import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import com.ecomarket.autenticacion.service.AutenticacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutenticacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutenticacionService autenticacionService;

    @MockBean
    private AutenticacionRepository autenticacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrarUsuario_deberiaRetornar201() throws Exception {
        Usuario usuario = new Usuario(1L, "Luis", "luis@mail.com", "1234", "cliente");

        when(autenticacionService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/autenticacion/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Luis"))
                .andExpect(jsonPath("$.email").value("luis@mail.com"))
                .andExpect(jsonPath("$.rol").value("cliente"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.login.href").exists())
                .andExpect(jsonPath("$._links.verificarEmail.href").exists());
    }


    @Test
    void loginUsuario_credencialesValidas_deberiaRetornar200() throws Exception {
        Usuario usuario = new Usuario(1L, "Luis", "luis@mail.com", "1234", "cliente");

        when(autenticacionService.login("luis@mail.com", "1234")).thenReturn(true);
        when(autenticacionService.obtenerUsuario("luis@mail.com")).thenReturn(usuario);

        mockMvc.perform(post("/api/autenticacion/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "luis@mail.com",
                                "password", "1234"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Luis"))
                .andExpect(jsonPath("$.email").value("luis@mail.com"))
                .andExpect(jsonPath("$.rol").value("cliente"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.verificarEmail.href").exists())
                .andExpect(jsonPath("$._links.registro.href").exists());
    }


    @Test
    void loginUsuario_credencialesInvalidas_deberiaRetornar401() throws Exception {
        when(autenticacionService.login("falso@mail.com", "mal")).thenReturn(false);

        mockMvc.perform(post("/api/autenticacion/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "falso@mail.com",
                                "password", "mal"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void verificarExistenciaEmail_deberiaRetornarBooleano() throws Exception {
        when(autenticacionRepository.findByEmail("existe@mail.com")).thenReturn(Optional.of(new Usuario()));

        mockMvc.perform(get("/api/autenticacion/existe")
                        .param("email", "existe@mail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
