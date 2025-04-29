package com.ecomarket.logistica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;
    private String direccionEntrega;
    private String estado; // "En preparación", "En camino", "Entregado"
    private LocalDate fechaEnvio;
}
