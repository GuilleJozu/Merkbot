package com.ls.merkbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GraficasRestController {

    @Autowired
    private VentasService servicioVenta;

    @Autowired
    private HistorialPrecioService servicioHistPrec;

    @Autowired HistorialPrecioRepository repHist;
    @Autowired VentasRepository repVentas;

    @GetMapping("/clientesPrecio")
    public List<HistorialPrecioPuntos> getClientePrecio() {
        return servicioHistPrec.getVistaPrevia(4L);
    }

    @GetMapping("competenciaComparativa")
    public List<HistorialPrecioPuntos> getCompetencia() {
        return servicioHistPrec.getComparativaCompetencia();
    }

    @GetMapping("/ventasGrafica")
    public List<VentasPuntos> getVentasGrafica(){
        return servicioVenta.getVentasPorProductoyCliente(4L,null);
    }

    @GetMapping("/detallada/clientesPrecio")
    public List<Object[]> clientesPrecioFiltrada(
            @RequestParam(required = false) Long producto,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaExacta
    ) {
        return repHist.getEvolucionFiltrada(producto, anio, mes);
    }

    // Comparativa contra competencia
    @GetMapping("/detallada/comparativaProductos")
    public List<Object[]> comparativaCompetencia(
            @RequestParam(required = false) Integer producto,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String keyword) {

        Integer p = (producto == null || producto.equals("") || producto.equals("Todos")) ? null : Integer.valueOf(producto);
        Integer y = (anio == null || anio.equals("") || anio.equals("Todos")) ? null : Integer.valueOf(anio);
        Integer m = (mes == null || mes.equals("") || mes.equals("Todos")) ? null : Integer.valueOf(mes);
        String k = (keyword == null || keyword.equals("") || keyword.equals("null")) ? null : keyword;

        return repHist.findComparativaCompetencia(p, y, m, k);
    }

    // Ventas por producto filtradas
    @GetMapping("/detallada/ventasPorCliente")
    public ResponseEntity<?> getVentasGrafica(
            @RequestParam(required = false) Long producto,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes
    ) {
        var data = repVentas.getVentasFiltradas(producto, anio, mes);
        return ResponseEntity.ok(data);
    }
}
