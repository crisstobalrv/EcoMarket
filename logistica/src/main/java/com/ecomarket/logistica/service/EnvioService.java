package com.ecomarket.logistica.service;

import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.repository.EnvioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {



    private final EnvioRepository envioRepo;
    private final RestTemplate restTemplate;

    public EnvioService(EnvioRepository envioRepo, RestTemplate restTemplate) {
        this.envioRepo = envioRepo;
        this.restTemplate = restTemplate;
    }


    public Envio crearEnvio(Envio envio) {
        // Verificar si ya hay un envío para ese pedido
        if (!envioRepo.findByPedidoId(envio.getPedidoId()).isEmpty()) {
            throw new RuntimeException("Ya existe un envío registrado para este pedido.");
        }

        // Verificar que el pedido exista en el microservicio de Pedidos
        if (!pedidoExiste(envio.getPedidoId())) {
            throw new RuntimeException("No se puede crear el envío: el pedido no existe.");
        }

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

    private boolean pedidoExiste(Long pedidoId) {
        try {
            String url = "http://localhost:8082/api/pedidos/" + pedidoId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

}
