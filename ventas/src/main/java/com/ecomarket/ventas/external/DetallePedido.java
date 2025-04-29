package com.ecomarket.ventas.external;

import lombok.Data;

@Data
public class DetallePedido {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
}
