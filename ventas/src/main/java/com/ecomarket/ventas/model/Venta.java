package com.ecomarket.ventas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;
    private Long clienteId;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private String medioPago;
    private String estado;
}
