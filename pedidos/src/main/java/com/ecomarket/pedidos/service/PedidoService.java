package com.ecomarket.pedidos.service;

import com.ecomarket.pedidos.external.Producto;
import com.ecomarket.pedidos.model.DetallePedido;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
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

        double total = 0;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Long productoId = detalle.getProductoId();
            Integer cantidad = detalle.getCantidad();

            // Verificar disponibilidad
            String disponibilidadUrl = inventarioBaseUrl + "/" + productoId + "/disponibilidad/" + cantidad;
            Boolean disponible = restTemplate.getForObject(disponibilidadUrl, Boolean.class);

            if (Boolean.FALSE.equals(disponible)) {
                throw new RuntimeException("Producto " + productoId + " no tiene stock suficiente.");
            }

            // Obtener precio real del producto
            String productoUrl = inventarioBaseUrl + "/" + productoId;
            Producto producto = restTemplate.getForObject(productoUrl, Producto.class);

            if (producto == null) {
                throw new RuntimeException("Producto " + productoId + " no encontrado en inventario.");
            }

            // Asignar el precio real
            detalle.setPrecioUnitario(producto.getPrecio());

            // Acumular el total
            total += producto.getPrecio() * cantidad;

            // Asignar la referencia al pedido
            detalle.setPedido(pedido);
        }

        pedido.setTotal(total);

        return pedidoRepo.save(pedido);
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
