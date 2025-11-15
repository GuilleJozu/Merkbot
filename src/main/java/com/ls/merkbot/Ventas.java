package com.ls.merkbot;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="ventas")
public class Ventas {

        @Id
        @GeneratedValue (strategy = GenerationType.IDENTITY)
        @Column(name = "id_venta")
        private Long idVenta;


        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Column
        private LocalDate fecha;

        @CreationTimestamp
        @Column(name= "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Column
        private LocalDateTime deletedAt;

        @Column
        private Boolean activo;

        @ManyToOne
        @JoinColumn(name = "id_usuario", nullable = false)
        private Usuarios usuario;

        @ManyToOne
        @JoinColumn(name = "id_cliente", nullable = false)
        private Clientes cliente;

        @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<VentaDetalles> Detalles;

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public List<VentaDetalles> getDetalles() {
        return Detalles;
    }

    public void setDetalles(List<VentaDetalles> Detalles) {
        this.Detalles = Detalles;
    }

    public Ventas orElseThrow() {
        return null;
    }
}



