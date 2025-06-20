package com.ecomarket.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorUpdateRequest {
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
}
