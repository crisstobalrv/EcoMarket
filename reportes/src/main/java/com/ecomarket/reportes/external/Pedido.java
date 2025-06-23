package com.ecomarket.reportes.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private Long clienteId;
    private LocalDate fecha;
    private String estado;
    private Double total;

    private List<DetallePedido> detalles;

}
