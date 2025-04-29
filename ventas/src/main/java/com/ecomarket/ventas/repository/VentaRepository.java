package com.ecomarket.ventas.repository;

import com.ecomarket.ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByClienteId(Long clienteId);
    List<Venta> findByPedidoId(Long pedidoId);
}
