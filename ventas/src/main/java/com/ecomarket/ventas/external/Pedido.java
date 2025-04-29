package com.ecomarket.ventas.external;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class Pedido {
    private Long id;
    private Long clienteId;
    private LocalDate fecha;
    private String estado;
    private Double total;

    // Esta es la parte importante:
    private List<DetallePedido> detalles;
}
