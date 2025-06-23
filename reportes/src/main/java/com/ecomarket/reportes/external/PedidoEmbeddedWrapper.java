package com.ecomarket.reportes.external;

import java.util.List;

public class PedidoEmbeddedWrapper {
    private Embedded _embedded;

    public Embedded get_embedded() {
        return _embedded;
    }

    public void set_embedded(Embedded _embedded) {
        this._embedded = _embedded;
    }

    public static class Embedded {
        private List<Pedido> pedidoList;

        public List<Pedido> getPedidoList() {
            return pedidoList;
        }

        public void setPedidoList(List<Pedido> pedidoList) {
            this.pedidoList = pedidoList;
        }
    }
}
