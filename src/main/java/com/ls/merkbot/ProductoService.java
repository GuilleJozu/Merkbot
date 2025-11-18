package com.ls.merkbot;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {
    private ProductosRepository repProductos;

    public ProductoService(ProductosRepository repProductos){
        this.repProductos = repProductos;
    }

    public List<Productos> listarProductos(){
       return repProductos.findActivas();
    }

    public Productos buscarPorId(Long id) {
        return repProductos.findById(id).orElse(null);
    }
    public Productos guardarProducto(Productos producto) {
        producto.setFechaHora(LocalDateTime.now());
        producto.setActivo(true);
        return repProductos.save(producto);
    }

    public Productos actualizarProducto(Productos producto) {
        Productos existente = repProductos.findById(producto.getId_producto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existente.setNombre(producto.getNombre());
        existente.setCompetencia(producto.getCompetencia());
        existente.setFoto(producto.getFoto());
        if (producto.getActivo() != null) {
            existente.setActivo(producto.getActivo());
        }

        existente.setUpdatedAt(LocalDateTime.now());

        return repProductos.save(existente);
    }

    public void eliminarLogico(long id) {
        Productos productos =buscarPorId(id);
        if (productos != null){
            productos.setActivo(false);
            productos.setDeletedAt(LocalDateTime.now());
            repProductos.save(productos);
        }
    }


}
