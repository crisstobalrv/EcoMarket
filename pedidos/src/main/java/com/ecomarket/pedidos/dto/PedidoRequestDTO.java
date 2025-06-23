package com.ecomarket.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Solicitud para crear un nuevo pedido")
public class PedidoRequestDTO {

    @Schema(example = "1", description = "ID del cliente")
    private Long clienteId;

    @Schema(description = "Lista de productos solicitados")
    private List<DetallePedidoDTO> detalles;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }
}
