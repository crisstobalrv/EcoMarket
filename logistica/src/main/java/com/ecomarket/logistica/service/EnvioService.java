package com.ecomarket.logistica.service;

import com.ecomarket.logistica.dto.CrearEnvioDTO;
import com.ecomarket.logistica.model.Envio;
import com.ecomarket.logistica.repository.EnvioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    public Envio crearDesdeDTO(CrearEnvioDTO dto) {
        Long ventaId = dto.getVentaId();
        String direccion = dto.getDireccionEntrega();
        System.out.println("DTO recibido: ventaId=" + dto.getVentaId() + ", direccion=" + dto.getDireccionEntrega());

        // Validar que ya no exista un envío para la venta
        if (!envioRepo.findByVentaId(ventaId).isEmpty()) {
            throw new RuntimeException("Ya existe un envío registrado para esta venta.");
        }

        // Validar existencia de la venta en microservicio
        try {
            String url = "http://localhost:8084/api/ventas/" + ventaId;
            restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No se puede crear el envío: la venta no existe.");
        } catch (Exception e) {
            throw new RuntimeException("Error al contactar el microservicio de ventas: " + e.getMessage());
        }

        // Validar dirección
        if (direccion == null || direccion.isBlank()) {
            throw new RuntimeException("La dirección de entrega no puede estar vacía.");
        }

        // Crear el envío
        Envio envio = new Envio();
        envio.setVentaId(ventaId);
        envio.setDireccionEntrega(direccion);
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

}

