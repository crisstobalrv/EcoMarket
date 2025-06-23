package com.ecomarket.reportes.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PedidoEmbeddedWrapper {
    @JsonProperty("_embedded")
    private EmbeddedPedidos _embedded;

    @Data
    public static class EmbeddedPedidos {
        @JsonProperty("pedidoList")
        private List<Pedido> pedidoList;
    }
}

