package com.ecomarket.reportes.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // "Ventas por fecha", "Pedidos por estado", etc.
    private LocalDate fechaGeneracion;

    @Column(length = 10000)
    private String datos; // JSON plano como string
}
