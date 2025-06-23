package com.ecomarket.ventas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para crear una venta")
public class CrearVentaDTO {

    @Schema(example = "21", description = "ID del pedido asociado a la venta")
    private Long pedidoId;

    @Schema(example = "tarjeta", description = "Medio de pago utilizado")
    private String medioPago;

    public CrearVentaDTO() {
    }

    public CrearVentaDTO(Long pedidoId, String medioPago) {
        this.pedidoId = pedidoId;
        this.medioPago = medioPago;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }
}
