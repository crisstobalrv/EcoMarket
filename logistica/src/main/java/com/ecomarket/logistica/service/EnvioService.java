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
        // Validar que no exista ya un envío para esta venta
        if (!envioRepo.findByVentaId(envio.getVentaId()).isEmpty()) {
            throw new RuntimeException("Ya existe un envío registrado para esta venta.");
        }

        // Validar que la venta exista (por lo tanto, que el pedido esté pagado)
        if (!ventaExiste(envio.getVentaId())) {
            throw new RuntimeException("No se puede crear el envío: la venta no existe.");
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

    public List<Envio> obtenerPorVenta(Long ventaId) {
        return envioRepo.findByVentaId(ventaId);
    }

    public Envio actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        envio.setEstado(nuevoEstado);
        return envioRepo.save(envio);
    }

    private boolean ventaExiste(Long ventaId) {
        try {
            String url = "http://localhost:8086/api/ventas/" + ventaId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace(); // <- para que veas el error exacto en consola
            return false;
        }
    }
}

