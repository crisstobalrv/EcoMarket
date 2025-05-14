package com.ecomarket.reportes.external;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Venta {
    private Long id;
    private Long pedidoId;
    private Long clienteId;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private String medioPago;
}
