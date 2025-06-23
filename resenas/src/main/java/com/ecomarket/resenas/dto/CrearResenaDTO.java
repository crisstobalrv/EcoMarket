package com.ecomarket.resenas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para crear una reseña")
public class CrearResenaDTO {

    @Schema(example = "1", description = "ID del producto")
    private Long productoId;

    @Schema(example = "2", description = "ID del cliente")
    private Long clienteId;

    @Schema(example = "5", description = "Puntuación del 1 al 5")
    private int puntuacion;

    @Schema(example = "Muy buen producto", description = "Comentario del cliente")
    private String comentario;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
