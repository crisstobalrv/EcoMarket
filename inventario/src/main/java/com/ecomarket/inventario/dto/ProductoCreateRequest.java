package com.ecomarket.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCreateRequest {
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer stock;
    private Double precio;
    private Long proveedorId;
}
