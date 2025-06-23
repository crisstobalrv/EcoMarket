package com.ecomarket.resenas.external;

public class PedidoResponse {
    private EmbeddedPedidos _embedded;

    public EmbeddedPedidos get_embedded() {
        return _embedded;
    }

    public void set_embedded(EmbeddedPedidos _embedded) {
        this._embedded = _embedded;
    }
}
