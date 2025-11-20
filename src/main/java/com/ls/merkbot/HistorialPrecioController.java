package com.ls.merkbot;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/precios")
@Controller
public class HistorialPrecioController {
    private final HistorialPrecioService servPrecios;
    private final ProductosRepository repProducto;
    private final ClientesRepository repClientes;
    private final UsuarioRepository repUsuario;

    public HistorialPrecioController(HistorialPrecioService servPrecios, ProductosRepository repProducto, ClientesRepository repClientes, UsuarioRepository repUsuario) {
        this.servPrecios = servPrecios;
        this.repProducto = repProducto;
        this.repClientes = repClientes;
        this.repUsuario = repUsuario;
    }

    @GetMapping
    public String listaPrecios(Model model){
        List<HistorialPrecios> histPrec = servPrecios.listarPrecios();
        model.addAttribute("histPrec", histPrec);
        return "historialPrecio/precio";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        HistorialPrecios histPrec = servPrecios.buscarPorId(id);
        if (histPrec == null) {
            return "redirect:/precios?error=notfound";
        }
        model.addAttribute("histPrec", histPrec);
        return "historialPrecio/detalle";
    }

    @GetMapping("/pdf/{id}")
    public void generarPdf(@PathVariable Long id, HttpServletResponse response) throws Exception {

        HistorialPrecios historial = servPrecios.findId(id).orElseThrow();

        File file = ResourceUtils.getFile("classpath:reports/historialPrecio.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        Map<String,Object> params = new HashMap<>();
        params.put("cliente", historial.getCliente().getNombre());
        params.put("producto", historial.getProducto().getNombre());
        params.put("precio", String.valueOf(historial.getPrecios()));
        params.put("fecha", historial.getCreatedAt().toString());
        params.put("usuario", historial.getUsuario().getNombre());
        params.put("descripcion", historial.getDescripcion());
        String imgName = historial.getProducto().getFoto();
        String imgPath = null;

        if (imgName != null && !imgName.isBlank()) {
            File imgFile = new File("imagenes/" + imgName);
            if (imgFile.exists()) {
                imgPath = imgFile.getAbsolutePath();
            } else {
                System.out.println("Imagen no encontrada: " + imgFile.getAbsolutePath());
            }
        }
        if (imgPath == null) {
        }

        params.put("imagen", imgPath);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=HistorialPrecio.pdf");

        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
    }


    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        HistorialPrecios hp = new HistorialPrecios();
        hp.setCliente(new Clientes());
        hp.setProducto(new Productos());
        hp.setUsuario(new Usuarios());
        model.addAttribute("histPrec", new HistorialPrecios());
        model.addAttribute("clientes", repClientes.findAll());
        model.addAttribute("productos", repProducto.findAll());;
        model.addAttribute("usuarios", repUsuario.findAll());
        return "historialPrecio/nuevo_precio";
    }

    @PostMapping
    public String guardarPrecio(@ModelAttribute HistorialPrecios historial,
                          @RequestParam("archivo") MultipartFile archivo) throws Exception {

        if (!archivo.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            historial.setFotoPruebas(nombreArchivo);
        }

        servPrecios.guardarPrecio(historial);
        return "redirect:/precios?creado=true";
    }

    @GetMapping("/editar/{id}")
    public String editarPrecio(@PathVariable Long id, Model model) {
        HistorialPrecios registro = servPrecios.buscarPorId(id);

        model.addAttribute("histPrec", registro);
        model.addAttribute("clientes", repClientes.findAll());
        model.addAttribute("productos", repProducto.findAll());
        model.addAttribute("usuarios", repUsuario.findAll());

        return "historialPrecio/editar_precio";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPrecio(@PathVariable Long id,
                             @ModelAttribute HistorialPrecios historialActualizado,
                             @RequestParam("archivo") MultipartFile archivo) throws Exception {

        HistorialPrecios original = servPrecios.buscarPorId(id);
        historialActualizado.setId_historial(id);


        if (!archivo.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);
            historialActualizado.setFotoPruebas(nombreArchivo);
        } else {
            historialActualizado.setFotoPruebas(original.getFotoPruebas());
        }

        servPrecios.actualizar(historialActualizado);
        return "redirect:/precios?editado=true";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPrecio(@PathVariable("id") Long id) {
        servPrecios.eliminarLogico(id);
        return "redirect:/precios?eliminado=true";
    }


}
