package com.ls.merkbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
    List<Ventas> findTop5ByOrderByFechaDesc();


    @Query(value = """
        SELECT c.nombre AS cliente, SUM(d.cantidad) AS cantidad
        FROM ventas v
        JOIN clientes c ON c.id_clientes = v.id_cliente
        JOIN venta_detalles d ON d.id_venta    = v.id_venta
        JOIN productos p ON p.id_producto = d.id_producto
        WHERE (:idProducto IS NULL OR p.id_producto = :idProducto)
        AND (:año IS NULL OR YEAR(v.fecha) = :año)
        GROUP BY c.nombre
        ORDER BY SUM(d.cantidad) DESC
""", nativeQuery = true)
    List<VentasPuntos> findVentasPorProductoYCliente(@Param("idProducto") Long idProducto, @Param("año") Integer año);


    @Query("""
    SELECT v FROM Ventas v
    LEFT JOIN FETCH v.Detalles d
    LEFT JOIN FETCH d.producto
    LEFT JOIN FETCH v.cliente
    LEFT JOIN FETCH v.usuario
    WHERE v.idVenta = :id
""")
    Optional<Ventas> findByIdConDetalles(@Param("id") Long id);

    @Query("SELECT v FROM Ventas v WHERE v.activo = true")
    List<Ventas> findActivas();

    @Query(value = """
        SELECT 
            c.nombre AS cliente,
            SUM(d.cantidad) AS cantidad
        FROM ventas v
        JOIN clientes c ON c.id_clientes = v.id_cliente
        JOIN venta_detalles d ON d.id_venta = v.id_venta
        JOIN productos p ON p.id_producto = d.id_producto
        WHERE 
            (:producto IS NULL OR p.id_producto = :producto)
            AND (:anio IS NULL OR YEAR(v.fecha) = :anio)
            AND (:mes IS NULL OR MONTH(v.fecha) = :mes)
        GROUP BY c.nombre
        ORDER BY cantidad DESC;
        """, nativeQuery = true)
    List<Object[]> getVentasFiltradas(Long producto, Integer anio, Integer mes);
}


