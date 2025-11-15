package com.ls.merkbot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface HistorialPrecioPuntos {
    String getProducto();
    String getCliente();
    BigDecimal getPrecio();
    Boolean getCompetencia();
    LocalDateTime getFecha();

}
