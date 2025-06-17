package com.ecomarket.clientes.repository;

import com.ecomarket.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);
    Optional<Cliente> findByRut(String rut);

}
