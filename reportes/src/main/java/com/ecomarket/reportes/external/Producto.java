package com.ecomarket.reportes.external;

import lombok.Data;

@Data
public class Producto {
    private Long id;
    private String nombre;
    private String categoria;
    private Integer stock;
    private Double precio;
}
