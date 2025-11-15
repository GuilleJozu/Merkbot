package com.ls.merkbot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductosController {

    private final ProductoService servProductos;

    public ProductosController(ProductoService servProductos) {
        this.servProductos = servProductos;
    }

    @GetMapping
    public String listarProductos(Model model) {
        List<Productos> productos = servProductos.listarProductos();
        model.addAttribute("productos", productos);
        return "productos/productos";
    }

    @GetMapping("/{id}")
    public String verDetalleClientes(@PathVariable("id") Long id, Model model) {
        Productos producto = servProductos.buscarPorId(id);
        if (producto == null) {
            return "redirect:/productos?error=notfound";
        }
        model.addAttribute("producto", producto);
        return "productos/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Productos());
        return "productos/nuevo_producto";
    }

    @PostMapping
    public String guardarProducto(@ModelAttribute Productos producto,
                                  @RequestParam("archivo") MultipartFile archivo) throws IOException {

        if (!archivo.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            producto.setFoto(nombreArchivo);
        }
        servProductos.guardarProducto(producto);
        return "redirect:/productos?exitoProducto=true";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model) {
        Productos producto = servProductos.buscarPorId(id);
        model.addAttribute("producto", producto);
        return "productos/editar_producto";
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable("id") Long id,
                                     @ModelAttribute Productos productoActualizado,
                                     @RequestParam("archivo") MultipartFile archivo) throws IOException {
        Productos producto = servProductos.buscarPorId(id);
        productoActualizado.setId_producto(id);
        if (!archivo.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
            Files.copy(archivo.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            productoActualizado.setFoto(nombreArchivo);
        } else {
            productoActualizado.setFoto(producto.getFoto());
        }
        servProductos.actualizarProducto(productoActualizado);
        return "redirect:/productos?editado=true";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id) {
        servProductos.eliminarLogico(id);
        return "redirect:/productos?eliminado=true";
    }

}





