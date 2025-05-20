package com.ecomarket.autenticacion.controller;

import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import com.ecomarket.autenticacion.service.AutenticacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

import java.util.Map;

@RestController
@RequestMapping("/api/autenticacion")
public class AutenticacionController {

    private final AutenticacionService autenticacionService;
    private final AutenticacionRepository autenticacionRepository;

    public AutenticacionController(AutenticacionService autenticacionService, AutenticacionRepository autenticacionRepository) {
        this.autenticacionService = autenticacionService;
        this.autenticacionRepository = autenticacionRepository;
    }

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

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeEmail(@RequestParam String email) {
        boolean existe = autenticacionRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(existe);
    }

}
