package com.ecomarket.resenas.repository;

import com.ecomarket.resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    List<Resena> findByClienteId(Long clienteId);
}
