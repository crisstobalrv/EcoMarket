package com.ecomarket.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto incluido en el pedido")
public class DetallePedidoDTO {

    @Schema(example = "3", description = "ID del producto")
    private Long productoId;

    @Schema(example = "2", description = "Cantidad solicitada")
    private int cantidad;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
