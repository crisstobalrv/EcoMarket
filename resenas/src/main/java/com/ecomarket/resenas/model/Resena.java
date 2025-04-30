package com.ecomarket.resenas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId;
    private Long clienteId;
    private Integer puntuacion; // 1 a 5 estrellas
    private String comentario;
    private LocalDate fecha;
}
