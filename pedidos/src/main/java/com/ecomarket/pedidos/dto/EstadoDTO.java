package com.ecomarket.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto para actualizar el estado del pedido")
public class EstadoDTO {

    @Schema(example = "Pagado", description = "Nuevo estado del pedido")
    private String estado;

    public EstadoDTO() {
    }

    public EstadoDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
