package com.ls.merkbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    private final VentasService serviceVentas;
    private final ProductosRepository repProducto;
    private final ClientesRepository repClientes;
    private final UsuarioRepository repUsuario;

    public VentasController(VentasService serviceVentas, ProductosRepository repProducto, ClientesRepository repClientes, UsuarioRepository repUsuario) {
        this.serviceVentas = serviceVentas;
        this.repProducto = repProducto;
        this.repClientes = repClientes;
        this.repUsuario = repUsuario;
    }

    @GetMapping
    public String listarVentas(Model model) {
        List<Ventas> ventas = serviceVentas.listarVentas();
        model.addAttribute("ventas", ventas);
        return "ventas/ventas";
    }

    @GetMapping("/{id}")
    public String verDetalleVenta(@PathVariable("id") Long id, Model model) {
        Ventas venta = serviceVentas.buscarPorId(id);
        if (venta == null) {
            return "redirect:/ventas?error=notfound";
        }
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", venta.getDetalles());
        return "ventas/detalle";
    }

    @GetMapping("/pdf/{id}")
    public void generarPDF(@PathVariable Long id, HttpServletResponse response) throws Exception {

        Ventas venta = serviceVentas.findId(id).orElseThrow();
        List<VentaDetalles> detalles = venta.getDetalles();

        InputStream reporte = getClass().getResourceAsStream("/reports/detalleVenta.jrxml");
        
        if (reporte == null) {
        throw new FileNotFoundException("No se encontró detalleVenta.jasper en /reports dentro del JAR");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reporte);

        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cliente", venta.getCliente().getNombre());
        parametros.put("fecha", venta.getFecha().toString());
        parametros.put("usuario", venta.getUsuario().getNombre());

        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(
                detalles.stream()
                        .map(d -> Map.of("producto", d.getProducto().getNombre(), "cantidad", d.getCantidad()))
                        .toList()
        );

        JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros, datasource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Venta_" + id + ".pdf");

        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
    }


    @GetMapping("/nuevo")
    public String nuevaVentaForm(Model model) throws JsonProcessingException {
        model.addAttribute("venta",new Ventas());
        model.addAttribute("clientes", repClientes.findAll());
        model.addAttribute("usuarios", repUsuario.findAll());
        List<Productos> productos = repProducto.findCompetencia();
        model.addAttribute("productos", productos);

        ObjectMapper mapper = new ObjectMapper();
        String productosJson = mapper.writeValueAsString(
                productos.stream()
                        .map(p -> Map.of(
                                "id_producto", p.getId_producto(),
                                "nombre", p.getNombre()
                        ))
                        .collect(Collectors.toList()));

        model.addAttribute("productosJson", productosJson);

        return "ventas/nueva_venta";
    }

    @PostMapping
    public String guardarVenta(@ModelAttribute Ventas venta) {
        serviceVentas.guardarVentaDetalle(venta);
        return "redirect:/ventas?exitoVenta=true";
    }

    @GetMapping("/editar/{id}")
    public String editarVenta(@PathVariable("id") Long id, Model model) throws JsonProcessingException {
        Ventas venta = serviceVentas.buscarPorId(id);
        if (venta == null) {
            return "redirect:/ventas?error=notfound";
        }

        model.addAttribute("venta", venta);
        model.addAttribute("clientes", repClientes.findAll());
        model.addAttribute("usuarios", repUsuario.findAll());
        List<Productos> productos = repProducto.findCompetencia();
        model.addAttribute("productos", productos);


        ObjectMapper mapper = new ObjectMapper();
        String productosJson = mapper.writeValueAsString(
                productos.stream()
                        .map(p -> Map.of(
                                "id_producto", p.getId_producto(),
                                "nombre", p.getNombre()
                        ))
                        .collect(Collectors.toList()));
        model.addAttribute("productosJson", productosJson);

        return "ventas/editar_venta";
    }


    @PostMapping("/editar/{id}")
    public String actualizarVenta(@PathVariable("id") Long id, @ModelAttribute Ventas ventaActualizada) {
        ventaActualizada.setIdVenta(id);
        serviceVentas.actualizarVentaDetalle(ventaActualizada);
        return "redirect:/ventas?editado=true";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable("id") Long id) {
        serviceVentas.eliminarLogico(id);
        return "redirect:/ventas?eliminado=true";
    }
}


