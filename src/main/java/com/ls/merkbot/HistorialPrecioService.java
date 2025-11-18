package com.ls.merkbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HistorialPrecioService {

    @Autowired
    private HistorialPrecioRepository repoHistPrec;


    public List<HistorialPrecioPuntos> getVistaPrevia (Long idProducto) {
        return repoHistPrec.findVistaPrevia(idProducto);
    }

    public List<HistorialPrecioPuntos> getComparativaCompetencia(){
        return repoHistPrec.findComparativaCompetencia();
    }

    public List<HistorialPrecios> listarPrecios(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repoHistPrec.findListaPrecioUsuario(username);
    }
    public HistorialPrecios buscarPorId(Long id) {
        return repoHistPrec.findById(id).orElse(null);
    }

    public Optional <HistorialPrecios> findId(Long id) {
        return repoHistPrec.findById(id);
    }

    public HistorialPrecios guardarPrecio(HistorialPrecios histPrec) {
        if (histPrec.getCreatedAt() == null) {
            histPrec.setCreatedAt(LocalDateTime.now());
            histPrec.setActivo(true);
        }

        return repoHistPrec.save(histPrec);
    }
    public HistorialPrecios actualizar(HistorialPrecios historialActualizado) {

        HistorialPrecios existente = repoHistPrec.findById(historialActualizado.getId_historial())
                .orElseThrow(() -> new RuntimeException("Historial no encontrado"));

        existente.setDescripcion(historialActualizado.getDescripcion());
        existente.setPrecios(historialActualizado.getPrecios());
        existente.setFotoPruebas(historialActualizado.getFotoPruebas());
        existente.setUsuario(historialActualizado.getUsuario());
        existente.setCliente(historialActualizado.getCliente());
        existente.setProducto(historialActualizado.getProducto());

        existente.setUpdatedAt(LocalDateTime.now());
        

        return repoHistPrec.save(existente);
    }

    public void eliminarLogico(long id) {
        HistorialPrecios precios = buscarPorId(id);
        if (precios != null) {
            precios.setActivo(false);
            precios.setDeletedAt(LocalDateTime.now());
            repoHistPrec.save(precios);
        }
    }

}
