package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.dto.PedidoRequestDTO;
import com.ecomarket.pedidos.external.Producto;
import com.ecomarket.pedidos.model.DetallePedido;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final RestTemplate restTemplate;

    public PedidoService(PedidoRepository pedidoRepo, RestTemplate restTemplate) {
        this.pedidoRepo = pedidoRepo;
        this.restTemplate = restTemplate;
    }

    public Pedido registrar(Pedido pedido) {
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");

        String inventarioBaseUrl = "http://localhost:8083/api/productos";

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new RuntimeException("El pedido no puede estar vacío.");
        }

        double total = 0;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Long productoId = detalle.getProductoId();
            Integer cantidad = detalle.getCantidad();

            if (cantidad == null || cantidad <= 0) {
                throw new RuntimeException("Cantidad inválida para el producto ID " + productoId);
            }

            // Verificar disponibilidad
            String disponibilidadUrl = inventarioBaseUrl + "/" + productoId + "/disponibilidad/" + cantidad;
            Boolean disponible;

            try {
                disponible = restTemplate.getForObject(disponibilidadUrl, Boolean.class);
            } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("Producto " + productoId + " no existe en el inventario.");
            } catch (Exception e) {
                throw new RuntimeException("Error al consultar disponibilidad del producto " + productoId);
            }

            if (Boolean.FALSE.equals(disponible)) {
                throw new RuntimeException("Producto " + productoId + " no tiene stock suficiente.");
            }

            // Obtener información del producto
            String productoUrl = inventarioBaseUrl + "/" + productoId;
            Producto producto;
            try {
                producto = restTemplate.getForObject(productoUrl, Producto.class);
            } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("Producto " + productoId + " no encontrado en inventario.");
            } catch (Exception e) {
                throw new RuntimeException("Error al consultar datos del producto " + productoId);
            }

            if (producto == null) {
                throw new RuntimeException("No se pudo recuperar información del producto " + productoId + ".");
            }

            // Asignar precio y calcular total
            detalle.setPrecioUnitario(producto.getPrecio());
            total += producto.getPrecio() * cantidad;

            // Asignar el pedido al detalle
            detalle.setPedido(pedido);
        }

        pedido.setTotal(total);
        return pedidoRepo.save(pedido);
    }


    public Pedido registrarDesdeDto(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());

        List<DetallePedido> detalles = dto.getDetalles().stream().map(d -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(d.getProductoId());
            detalle.setCantidad(d.getCantidad());
            detalle.setPedido(pedido);
            return detalle;
        }).toList();

        pedido.setDetalles(detalles);

        return registrar(pedido); // llamas al método original
    }



    public List<Pedido> listarTodos() {
        return pedidoRepo.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepo.findById(id);
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        return pedidoRepo.save(pedido);
    }

    public void eliminarPorId(Long id) {
        pedidoRepo.deleteById(id);
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepo.findByClienteId(clienteId);
    }


}
