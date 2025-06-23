package com.ecomarket.ventas.service;

import com.ecomarket.ventas.dto.CrearVentaDTO;
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
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepo;
    private final RestTemplate restTemplate;

    public VentaService(VentaRepository ventaRepo, RestTemplate restTemplate) {
        this.ventaRepo = ventaRepo;
        this.restTemplate = restTemplate;
    }

    public Venta registrarVentaDesdeDTO(CrearVentaDTO dto) {
        Long pedidoId = dto.getPedidoId();
        String medioPago = dto.getMedioPago();

        if (pedidoId == null || medioPago == null || medioPago.isBlank()) {
            throw new RuntimeException("Faltan datos obligatorios para registrar la venta.");
        }

        // Verificar si ya existe una venta asociada al pedido
        if (!ventaRepo.findByPedidoId(pedidoId).isEmpty()) {
            throw new RuntimeException("El pedido ya fue pagado. No se puede registrar otra venta.");
        }

        // Validar medio de pago
        if (!medioPago.matches("(?i)efectivo|tarjeta|transferencia")) {
            throw new RuntimeException("Medio de pago no válido");
        }

        // Obtener información del pedido desde microservicio de Pedidos
        String pedidoUrl = "http://localhost:8089/api/pedidos/" + pedidoId;
        Pedido pedido = restTemplate.getForObject(pedidoUrl, Pedido.class);
        if (pedido == null) {
            throw new RuntimeException("No se pudo obtener el pedido desde el microservicio de Pedidos.");
        }

        // Cambiar estado del pedido a 'Pagado'
        String patchUrl = "http://localhost:8089/api/pedidos/" + pedidoId + "/estado";
        restTemplate.put(patchUrl, Map.of("estado", "Pagado"));

        // Descontar stock por cada producto
        for (DetallePedido detalle : pedido.getDetalles()) {
            String urlDescontar = "http://localhost:8083/api/productos/" + detalle.getProductoId()
                    + "/descontar/" + detalle.getCantidad();
            restTemplate.put(urlDescontar, null);
        }

        Venta venta = new Venta();
        venta.setPedidoId(pedidoId);
        venta.setClienteId(pedido.getClienteId());
        venta.setMedioPago(medioPago);
        venta.setFechaVenta(LocalDate.now());
        venta.setTotalVenta(pedido.getTotal());
        venta.setEstado("Pagada");

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

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepo.findById(id);
    }

    public boolean anularVenta(Long id) {
        Optional<Venta> ventaOpt = ventaRepo.findById(id);

        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            if (!"Anulada".equalsIgnoreCase(venta.getEstado())) {
                venta.setEstado("Anulada");
                ventaRepo.save(venta);
                return true;
            }
        }
        return false;
    }



}
