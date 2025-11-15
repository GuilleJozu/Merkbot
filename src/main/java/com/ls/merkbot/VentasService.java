package com.ls.merkbot;

import com.mysql.cj.xdevapi.Client;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentasService {
    private VentasRepository repVentas;
    private ProductosRepository repProductos;
    private UsuarioRepository repUsuario;

    public VentasService(VentasRepository repVentas, ProductosRepository repProductos, UsuarioRepository repUsuario) {
        this.repVentas = repVentas;
        this.repProductos = repProductos;
        this.repUsuario = repUsuario;
    }

    public List<Ventas> listarVentas() {
        return repVentas.findActivas();
    }

    public Ventas guardarVentaDetalle(Ventas venta) {
        venta.setCreatedAt(LocalDateTime.now());
        venta.setActivo(true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String nombreUsuario = auth.getName();
            repUsuario.findByUsuario(nombreUsuario).ifPresent(venta::setUsuario);

        }

        for (VentaDetalles detalle: venta.getDetalles()) {
            detalle.setVenta(venta);
        }

        return repVentas.save(venta);
    }

    public Ventas actualizarVentaDetalle (Ventas venta) {
        Ventas existente = repVentas.findById(venta.getIdVenta()).orElse(null);
        if (existente == null) return null;

        existente.setCliente(venta.getCliente());
        existente.setFecha(venta.getFecha());
        existente.setUpdatedAt(LocalDateTime.now());
        if (venta.getActivo() != null) {
            existente.setActivo(venta.getActivo());
        }


                if (venta.getDetalles() != null) {
            existente.getDetalles().clear();

            for (VentaDetalles detalle : venta.getDetalles()){
                detalle.setVenta(existente);
                existente.getDetalles().add(detalle);
            }

        }
        return repVentas.save(existente);

    }

    public void eliminarLogico(long id) {
        Ventas venta =buscarPorId(id);
        if (venta != null){
            venta.setActivo(false);
            venta.setDeletedAt(LocalDateTime.now());
            repVentas.save(venta);
        }
    }

    public List<Ventas> getUltimasVentas() {
        return repVentas.findTop5ByOrderByFechaDesc();
    }

    public List<VentasPuntos> getVentasPorProductoyCliente(Long idProducto,Integer año) {
        return repVentas.findVentasPorProductoYCliente(idProducto, año);
    }

    public Ventas buscarPorId(Long id) {
        return repVentas.findByIdConDetalles(id).orElse(null);
    }

    public Optional <Ventas> findId(Long id) {
        return repVentas.findByIdConDetalles(id);
    }



}

