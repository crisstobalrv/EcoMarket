package com.ecomarket.logistica.dto;

public class ActualizarEstadoDTO {
    private String estado;

    public ActualizarEstadoDTO() {}

    public ActualizarEstadoDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
