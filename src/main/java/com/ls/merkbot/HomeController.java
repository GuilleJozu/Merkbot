package com.ls.merkbot;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HistorialPrecioService HistPrecServicio;

    @Autowired
    private VentasService servVentas;


    @GetMapping ("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("ultimasVentas", servVentas.getUltimasVentas());
        return "index";
        }

    @GetMapping("/registros")
    public String registros() {
        return "registros";
    }

    @GetMapping("/graficas/detallada/evolucion")
    public String detalleEvolucion() {
        return "detalle-evolucion";
    }

    @GetMapping("/graficas/detallada/competencia")
    public String detalleCompetencia() {
        return "detalle-competencia";
    }




}

