package com.ecomarket.reportes.external;

import lombok.Data;
import java.util.List;

@Data
public class VentasWrapper {
    private Embedded _embedded;

    @Data
    public static class Embedded {
        private List<Venta> ventaList;
    }

}
