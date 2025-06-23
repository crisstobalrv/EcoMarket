package com.ecomarket.autenticacion.controller;

import com.ecomarket.autenticacion.dto.LoginRequest;
import com.ecomarket.autenticacion.dto.RegistroRequest;
import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import com.ecomarket.autenticacion.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest datos) {
        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.getNombre());
        nuevo.setEmail(datos.getEmail());
        nuevo.setPassword(datos.getPassword());
        nuevo.setRol("cliente"); // 🔐 asignado automáticamente

        Usuario creado = autenticacionService.registrar(nuevo);

        EntityModel<Usuario> model = EntityModel.of(creado);
        model.add(linkTo(methodOn(AutenticacionController.class).registrar(datos)).withSelfRel());
        model.add(linkTo(methodOn(AutenticacionController.class).login(new LoginRequest(creado.getEmail(), creado.getPassword()))).withRel("login"));

        return ResponseEntity.status(201).body(model);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (autenticacionService.login(email, password)) {
            Usuario usuario = autenticacionService.obtenerUsuario(email);

            EntityModel<Usuario> model = EntityModel.of(usuario,
                    linkTo(methodOn(AutenticacionController.class).login(request)).withSelfRel(),
                    linkTo(methodOn(AutenticacionController.class).registrar(new RegistroRequest())).withRel("registro")
            );

            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @Hidden
    @GetMapping("/existe")
    @Operation(summary = "Verificar si un email ya está registrado")
    public ResponseEntity<Boolean> existeEmail(@RequestParam String email) {
        boolean existe = autenticacionRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(existe);
    }
}
