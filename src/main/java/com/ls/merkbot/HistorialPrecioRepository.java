package com.ls.merkbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Repository
public interface HistorialPrecioRepository extends JpaRepository <HistorialPrecios, Long> {


    @Query(value = """
            SELECT
            p.nombre AS producto,
            c.nombre AS cliente,
            hp.precios AS precio,
            hp.created_at AS fecha
            FROM historial_precios hp
            JOIN productos p ON p.id_producto = hp.id_producto
            JOIN clientes  c ON c.id_clientes = hp.id_cliente
            WHERE hp.id_producto = :idProducto
            ORDER BY hp.created_at ASC
            """, nativeQuery = true)
    List<HistorialPrecioPuntos> findVistaPrevia(@Param("idProducto") Long idProducto);

    @Query(value = """
        SELECT 
        p.nombre AS producto,
        c.nombre AS cliente,
        hp.precios AS precio,
        hp.created_at AS fecha,
        p.competencia AS competencia
        FROM historial_precios hp
        JOIN productos p ON p.id_producto = hp.id_producto
        JOIN clientes c ON c.id_clientes = hp.id_cliente
        ORDER BY hp.created_at ASC
        """, nativeQuery = true)
    List<HistorialPrecioPuntos> findComparativaCompetencia();

    @Query(value = """
                 SELECT h FROM HistorialPrecios h
        JOIN FETCH h.producto
        JOIN FETCH h.usuario
        JOIN FETCH h.cliente
        WHERE h.usuario.usuario = :username
        AND h.activo = true
        ORDER BY h.createdAt DESC
       """)
    List<HistorialPrecios> findListaPrecioUsuario(@Param("username") String username);

    @Query("""
    SELECT h.createdAt, c.nombre, h.precios
    FROM HistorialPrecios h
    JOIN h.cliente c
    JOIN h.producto p
    WHERE (:producto IS NULL OR p.id_producto = :producto)
    AND (:anio IS NULL OR YEAR(h.createdAt) = :anio)
    AND (:mes IS NULL OR MONTH(h.createdAt) = :mes)
    ORDER BY h.createdAt ASC
    """)
    List<Object[]> getEvolucionFiltrada(Long producto, Integer anio, Integer mes);


    @Query(value = """
        SELECT 
            hp.created_at AS fecha,
            p.nombre AS producto,
            hp.precios AS precio,
            p.competencia AS competencia
        FROM historial_precios hp
        JOIN productos p ON p.id_producto = hp.id_producto
        WHERE 
            (:producto IS NULL OR p.id_producto = :producto)
            AND (:anio IS NULL OR YEAR(hp.created_at) = :anio)
            AND (:mes IS NULL OR MONTH(hp.created_at) = :mes)
            AND (:keyword IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY hp.created_at ASC
        """, nativeQuery = true)
    List<Object[]> findComparativaCompetencia(
            @Param("producto") Integer producto,
            @Param("anio") Integer anio,
            @Param("mes") Integer mes,
            @Param("keyword") String keyword);
}