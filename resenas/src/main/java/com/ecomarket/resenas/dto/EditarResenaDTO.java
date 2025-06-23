package com.ecomarket.resenas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para editar una reseña existente")
public class EditarResenaDTO {

    @Schema(example = "4", description = "Puntuación actualizada del 1 al 5")
    private int puntuacion;

    @Schema(example = "Producto bueno, pero llegó con retraso", description = "Comentario actualizado")
    private String comentario;

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
