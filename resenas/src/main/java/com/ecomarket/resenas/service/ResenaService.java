package com.ecomarket.resenas.service;

import com.ecomarket.resenas.external.Cliente;
import com.ecomarket.resenas.external.Pedido;
import com.ecomarket.resenas.external.Producto;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepo;
    private final RestTemplate restTemplate;

    public ResenaService(ResenaRepository resenaRepo, RestTemplate restTemplate) {
        this.resenaRepo = resenaRepo;
        this.restTemplate = restTemplate;
    }

    public Resena crear(Resena resena) {
        // Validar existencia de producto
        try {
            String urlProducto = "http://localhost:8083/api/productos/" + resena.getProductoId();
            restTemplate.getForObject(urlProducto, Void.class); // Solo comprobación
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("El producto no existe.");
        }


        try {
            String urlCliente = "http://localhost:8087/api/clientes/" + resena.getClienteId();
            restTemplate.getForObject(urlCliente, Void.class); // Solo para verificar existencia
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("El cliente no existe.");
        }


        // Validar que el cliente haya comprado el producto
        String urlPedidos = "http://localhost:8082/api/pedidos/cliente/" + resena.getClienteId();
        Pedido[] pedidos = restTemplate.getForObject(urlPedidos, Pedido[].class);
        boolean loCompro = Arrays.stream(pedidos)
                .flatMap(p -> p.getDetalles().stream())
                .anyMatch(d -> d.getProductoId().equals(resena.getProductoId()));

        if (!loCompro) {
            throw new RuntimeException("El cliente no ha comprado este producto.");
        }

        // Validaciones extra (comentario, puntuación, etc.)
        if (resena.getComentario() == null || resena.getComentario().isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío.");
        }

        if (resena.getPuntuacion() < 1 || resena.getPuntuacion() > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5.");
        }

        resena.setFecha(LocalDate.now());
        return resenaRepo.save(resena);
    }


    public List<Resena> listarTodas() {
        return resenaRepo.findAll();
    }

    public List<Resena> listarPorProducto(Long productoId) {
        return resenaRepo.findByProductoId(productoId);
    }

    public List<Resena> listarPorCliente(Long clienteId) {
        return resenaRepo.findByClienteId(clienteId);
    }

    public Optional<Resena> buscarPorId(Long id) {
        return resenaRepo.findById(id);
    }

    public Resena editar(Long id, Resena nuevaResena) {
        Resena actual = resenaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        actual.setPuntuacion(nuevaResena.getPuntuacion());
        actual.setComentario(nuevaResena.getComentario());
        actual.setFecha(LocalDate.now());

        return resenaRepo.save(actual);
    }

    public void eliminar(Long id) {
        resenaRepo.deleteById(id);
    }
}
