package com.ecomarket.resenas.service;

import com.ecomarket.resenas.dto.CrearResenaDTO;
import com.ecomarket.resenas.dto.EditarResenaDTO;
import com.ecomarket.resenas.external.Pedido;
import com.ecomarket.resenas.external.PedidoResponse;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
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

    public Resena crearDesdeDTO(CrearResenaDTO dto) {
        Long productoId = dto.getProductoId();
        Long clienteId = dto.getClienteId();

        // Validar existencia de producto
        try {
            String urlProducto = "http://localhost:8083/api/productos/" + productoId;
            restTemplate.getForObject(urlProducto, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("El producto no existe.");
        }

        // Validar existencia de cliente
        try {
            String urlCliente = "http://localhost:8087/api/clientes/" + clienteId;
            restTemplate.getForObject(urlCliente, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("El cliente no existe.");
        }

        // Obtener pedidos y validar compra del producto
        String urlPedidos = "http://localhost:8089/api/pedidos/cliente/" + clienteId;
        PedidoResponse response = restTemplate.getForObject(urlPedidos, PedidoResponse.class);

        List<Pedido> pedidos = Optional.ofNullable(response)
                .map(PedidoResponse::get_embedded)
                .map(embedded -> embedded.getPedidoList())
                .orElse(List.of());

        boolean loCompro = pedidos.stream()
                .flatMap(p -> p.getDetalles().stream())
                .anyMatch(d -> d.getProductoId().equals(productoId));

        if (!loCompro) {
            throw new RuntimeException("El cliente no ha comprado este producto.");
        }

        // Validaciones extra
        if (dto.getComentario() == null || dto.getComentario().isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío.");
        }

        if (dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5.");
        }

        boolean yaExiste = resenaRepo.findByClienteIdAndProductoId(clienteId, productoId).isPresent();
        if (yaExiste) {
            throw new RuntimeException("Ya existe una reseña de este cliente para este producto.");
        }

        Resena nueva = new Resena();
        nueva.setProductoId(productoId);
        nueva.setClienteId(clienteId);
        nueva.setComentario(dto.getComentario());
        nueva.setPuntuacion(dto.getPuntuacion());
        nueva.setFecha(LocalDate.now());

        return resenaRepo.save(nueva);
    }



    public void editarDesdeDTO(Long id, EditarResenaDTO dto) {
        Resena actual = resenaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        if (dto.getComentario() == null || dto.getComentario().isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío.");
        }

        if (dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5.");
        }

        actual.setComentario(dto.getComentario());
        actual.setPuntuacion(dto.getPuntuacion());
        actual.setFecha(LocalDate.now());

        resenaRepo.save(actual);
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

    public void eliminar(Long id) {
        resenaRepo.deleteById(id);
    }
}
