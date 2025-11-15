package com.ls.merkbot;


import jakarta.persistence.*;

@Entity
@Table(name= "venta_detalles")
public class VentaDetalles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
    private Long idDetalle;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Ventas venta;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Productos producto;

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Ventas getVenta() {
        return venta;
    }

    public void setVenta(Ventas venta) {
        this.venta = venta;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }
}
