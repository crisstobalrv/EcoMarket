package com.ecomarket.resenas.external;

import lombok.Data;

import java.util.List;

@Data
public class Pedido {
    private Long clienteId;
    private List<DetallePedido> detalles;
}
