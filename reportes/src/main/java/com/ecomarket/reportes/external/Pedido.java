package com.ecomarket.reportes.external;

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

    private List<DetallePedido> detalles;

}
