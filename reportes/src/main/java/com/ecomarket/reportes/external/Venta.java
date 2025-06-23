package com.ecomarket.reportes.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    private Long id;
    private Long pedidoId;
    private Long clienteId;
    private LocalDate fecha;
    private Double totalVenta;
    private String medioPago;
}
