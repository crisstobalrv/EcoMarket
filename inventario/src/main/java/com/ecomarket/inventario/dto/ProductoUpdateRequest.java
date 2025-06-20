package com.ecomarket.inventario.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer stock;
    private Double precio;
    private Long proveedorId;
}
