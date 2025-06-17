package com.ecomarket.autenticacion.controller;

import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import com.ecomarket.autenticacion.service.AutenticacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/autenticacion")
@Tag(name = "Autenticación", description = "Operaciones de login y registro")
public class AutenticacionController {

    private final AutenticacionService autenticacionService;
    private final AutenticacionRepository autenticacionRepository;

    public AutenticacionController(AutenticacionService autenticacionService, AutenticacionRepository autenticacionRepository) {
        this.autenticacionService = autenticacionService;
        this.autenticacionRepository = autenticacionRepository;
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema si el email no está registrado"
    )
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "409", description = "Error de validación o email ya registrado")
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario creado = autenticacionService.registrar(usuario);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "El usuario ha sido registrado exitosamente.");
            respuesta.put("usuarioId", creado.getId());
            respuesta.put("nombre", creado.getNombre());
            respuesta.put("email", creado.getEmail());
            respuesta.put("rol", creado.getRol());

            return ResponseEntity.status(201).body(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of(
                    "error", "Registro fallido: " + e.getMessage()
            ));
        }
    }

    @Operation(
            summary = "Login de usuario",
            description = "Valida las credenciales del usuario y devuelve información básica si son correctas"
    )
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (autenticacionService.login(email, password)) {
            Usuario usuario = autenticacionService.obtenerUsuario(email);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Login exitoso",
                    "usuarioId", usuario.getId(),
                    "rol", usuario.getRol()
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @Operation(
            summary = "Verificar existencia de email",
            description = "Devuelve true si el email ya está registrado, false si no"
    )
    @ApiResponse(responseCode = "200", description = "Resultado de verificación")
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeEmail(@RequestParam String email) {
        boolean existe = autenticacionRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(existe);
    }
}
