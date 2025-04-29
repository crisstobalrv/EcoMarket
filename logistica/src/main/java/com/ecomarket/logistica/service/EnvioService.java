package com.ecomarket.logistica.service;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private final EnvioRepository envioRepo;

    public EnvioService(EnvioRepository envioRepo) {
        this.envioRepo = envioRepo;
    }

    public Envio crearEnvio(Envio envio) {
        envio.setEstado("En preparación");
        envio.setFechaEnvio(LocalDate.now());
        return envioRepo.save(envio);
    }

    public List<Envio> obtenerTodos() {
        return envioRepo.findAll();
    }

    public Optional<Envio> obtenerPorId(Long id) {
        return envioRepo.findById(id);
    }

    public List<Envio> obtenerPorPedido(Long pedidoId) {
        return envioRepo.findByPedidoId(pedidoId);
    }

    public Envio actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        envio.setEstado(nuevoEstado);
        return envioRepo.save(envio);
    }
}
