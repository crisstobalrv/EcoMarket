package com.ecomarket.reportes.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    private Long productoId;
    private Integer cantidad;
}
