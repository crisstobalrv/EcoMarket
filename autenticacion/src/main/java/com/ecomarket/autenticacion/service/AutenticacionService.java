package com.ecomarket.autenticacion.service;

import com.ecomarket.autenticacion.model.Usuario;
import com.ecomarket.autenticacion.repository.AutenticacionRepository;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private final AutenticacionRepository autenticacionRepository;

    public AutenticacionService(AutenticacionRepository autenticacionRepository) {
        this.autenticacionRepository = autenticacionRepository;
    }

    public Usuario registrar(Usuario nuevo) {
        if (nuevo.getNombre() == null || nuevo.getNombre().isBlank()) {
            throw new RuntimeException("El nombre no puede estar vacío.");
        }

        if (!nuevo.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new RuntimeException("Formato de correo no válido.");
        }

        if (autenticacionRepository.findByEmail(nuevo.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        if (nuevo.getPassword().length() < 3) {
            throw new RuntimeException("La contraseña debe tener al menos 3 caracteres.");
        }

        return autenticacionRepository.save(nuevo);
    }



    public boolean login(String email, String password) {
        return autenticacionRepository.findByEmail(email)
                .map(usuario -> usuario.getPassword().equals(password))
                .orElse(false);
    }

    public Usuario obtenerUsuario(String email) {
        return autenticacionRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

