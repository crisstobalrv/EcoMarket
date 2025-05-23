package com.ecomarket.autenticacion.repository;

import com.ecomarket.autenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutenticacionRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
