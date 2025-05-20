package com.ecomarket.inventario.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer stock;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JsonIgnoreProperties("productos")
    private Proveedor proveedor;


}
