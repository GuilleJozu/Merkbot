package com.ls.merkbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/clientes")
@Controller
public class ClientesController {

    private ClientesService servClientes;

    public ClientesController(ClientesService servClientes){
        this.servClientes = servClientes;
    }

    @GetMapping
    public String listarClientes(Model model){
        List<Clientes> clientes = servClientes.listarClientes();
        model.addAttribute("clientes", clientes);
        return "clientes/clientes";
    }

    @GetMapping("/{id}")
    public String verDetalleClientes(@PathVariable("id") Long id, Model model) {
        Clientes cliente = servClientes.buscarPorId(id);
        if (cliente == null) {
            return "redirect:/clientes?error=notfound";
        }
        model.addAttribute("cliente", cliente);
        return "clientes/detalle";
    }
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Clientes());
        return "clientes/nuevo_cliente";
    }

    @PostMapping
    public String guardarClientes(@ModelAttribute Clientes cliente) {
        servClientes.guardarClientes(cliente);
        return "redirect:/clientes?exitoProducto=true";
    }

    @GetMapping("/editar/{id}")
    public String editarClientes(@PathVariable("id") Long id, Model model) {
        Clientes cliente = servClientes.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "clientes/editar_cliente";
    }

    @PostMapping("/editar/{id}")
    public String actualizarClientes(@PathVariable("id") Long id,
                                     @ModelAttribute Clientes clienteActualizado) {
        Clientes cliente = servClientes.buscarPorId(id);
        clienteActualizado.setId_clientes(id);
        servClientes.actualizarClientes(clienteActualizado);
        return "redirect:/clientes?editado=true";

    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id) {
        servClientes.eliminarLogico(id);
        return "redirect:/clientes?eliminado=true";
    }
}

