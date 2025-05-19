package com.ecomarket.ventas.service;

import com.ecomarket.ventas.external.DetallePedido;
import com.ecomarket.ventas.external.Pedido;
import com.ecomarket.ventas.model.Venta;
import com.ecomarket.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VentaService {

    private final VentaRepository ventaRepo;
    private final RestTemplate restTemplate;

    public VentaService(VentaRepository ventaRepo, RestTemplate restTemplate) {
        this.ventaRepo = ventaRepo;
        this.restTemplate = restTemplate;
    }

    public Venta registrarVenta(Venta venta) {
        // Verificar si ya existe una venta asociada al pedido
        List<Venta> ventasExistentes = ventaRepo.findByPedidoId(venta.getPedidoId());

        if (!ventasExistentes.isEmpty()) {
            throw new RuntimeException("El pedido ya fue pagado. No se puede registrar otra venta.");
        }

        // Validar medio de pago
        if (!venta.getMedioPago().matches("(?i)efectivo|tarjeta|transferencia")) {
            throw new RuntimeException("Medio de pago no válido");
        }

        if (venta.getPedidoId() == null || venta.getMedioPago() == null) {
            throw new RuntimeException("Faltan datos obligatorios para registrar la venta.");
        }


        venta.setFechaVenta(LocalDate.now());

        // Obtener información del pedido desde microservicio de Pedidos
        String pedidoUrl = "http://localhost:8089/api/pedidos/" + venta.getPedidoId();
        Pedido pedido = restTemplate.getForObject(pedidoUrl, Pedido.class);

        if (pedido == null) {
            throw new RuntimeException("No se pudo obtener el pedido desde el microservicio de Pedidos.");
        }

        venta.setTotalVenta(pedido.getTotal());
        venta.setClienteId(pedido.getClienteId());

        // Cambiar estado del pedido a 'Pagado'
        String patchUrl = "http://localhost:8089/api/pedidos/" + venta.getPedidoId() + "/estado";
        restTemplate.put(patchUrl,
                java.util.Collections.singletonMap("estado", "Pagado"));

        // Descontar stock por cada producto
        for (DetallePedido detalle : pedido.getDetalles()) {
            String urlDescontar = "http://localhost:8083/api/productos/" + detalle.getProductoId()
                    + "/descontar/" + detalle.getCantidad();
            restTemplate.put(urlDescontar, null);
        }

        return ventaRepo.save(venta);
    }



    public List<Venta> obtenerTodas() {
        return ventaRepo.findAll();
    }

    public List<Venta> obtenerPorCliente(Long clienteId) {
        return ventaRepo.findByClienteId(clienteId);
    }


    public Map<String, Object> generarFactura(Long idVenta) {
        Venta venta = ventaRepo.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        Map<String, Object> factura = new LinkedHashMap<>();
        factura.put("numeroFactura", "F-" + venta.getId());
        factura.put("fecha", venta.getFechaVenta());
        factura.put("clienteId", venta.getClienteId());
        factura.put("pedidoId", venta.getPedidoId());
        factura.put("medioPago", venta.getMedioPago());
        factura.put("total", venta.getTotalVenta());

        return factura;
    }

}
