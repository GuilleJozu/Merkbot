package com.ls.merkbot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioService servUsuario;
    private final RolesRepository repRol;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuariosController(UsuarioService servUsuario, RolesRepository repRol) {
        this.servUsuario = servUsuario;
        this.repRol = repRol;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuarios> usuarios = servUsuario.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/usuarios";
    }

    @GetMapping("/{id}")
    public String verDetalleUsuarios(@PathVariable("id") Long id, Model model) {
        Usuarios usuario = servUsuario.buscarPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios?error=notfound";
        }
        model.addAttribute("usuario", usuario);
        return "usuarios/detalle";
    }
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuarios());
        model.addAttribute("roles", repRol.findAll());
        return "usuarios/nuevo_usuario";
    }

    @PostMapping
    public String guardarUsuarios(@ModelAttribute Usuarios usuario) {
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        servUsuario.guardarUsuarios(usuario);
        return "redirect:/usuarios?exitoUsuario=true";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarios(@PathVariable("id") Long id, Model model) {
        Usuarios usuario = servUsuario.buscarPorId(id);
        model.addAttribute("roles", repRol.findAll());
        model.addAttribute("usuario", usuario);
        return "usuarios/editar_usuario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarClientes(@PathVariable("id") Long id,
                                     @ModelAttribute Usuarios usuarioActualizado,
                                     @RequestParam("nuevaContra")String nuevaContra) {
        Usuarios usuario = servUsuario.buscarPorId(id);
        usuarioActualizado.setId_user(id);

        if (nuevaContra == null || nuevaContra.isBlank()) {
            usuarioActualizado.setContraseña(usuario.getContraseña());
        } else {
            usuarioActualizado.setContraseña(passwordEncoder.encode(nuevaContra));
        }
        servUsuario.actualizarUsuario(usuarioActualizado);
        return "redirect:/usuarios?editado=true";

    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id) {
        servUsuario.eliminarLogico(id);
        return "redirect:/usuarios?eliminado=true";
    }
}
