package com.ecomarket.logistica.repository;

import com.ecomarket.logistica.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByVentaId(Long ventaId);

}
