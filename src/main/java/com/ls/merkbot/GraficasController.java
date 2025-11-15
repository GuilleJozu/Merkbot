package com.ls.merkbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/graficas/detalles")
public class GraficasController {
    @Autowired
    private ProductosRepository repProd;
    private Model model;

    @GetMapping("/clientePrecio")
    public String clientePrecio(Model model) {
        this.model = model;
        model.addAttribute("titulo", "Precio entre Clientes");
        model.addAttribute("productos", repProd.findAll());
        model.addAttribute("anios", List.of(2023, 2024, 2025));
        return "graficas/clientePrecioDetallada";
    }

    @GetMapping("/competenciaComparativa")
    public String competenciaComparativa(Model model) {
        model.addAttribute("titulo", "Comparativa de Precios entre Competencia");
        model.addAttribute("productos", repProd.findAll());
        model.addAttribute("anios", List.of(2023, 2024, 2025));
        return "graficas/competenciaComparativa";
    }
    @GetMapping("/ventasTotal")
    public String ventasTotal(Model model) {
        model.addAttribute("titulo", "Total de cajas vendidas");
        model.addAttribute("productos", repProd.findAll());
        model.addAttribute("anios", List.of(2023, 2024, 2025));
        return "graficas/ventasTotal";
    }

    private List<Map<String,Object>> meses() {
        return List.of(
                map(1,"Enero"), map(2,"Febrero"), map(3,"Marzo"), map(4,"Abril"),
                map(5,"Mayo"), map(6,"Junio"), map(7,"Julio"), map(8,"Agosto"),
                map(9,"Septiembre"), map(10,"Octubre"), map(11,"Noviembre"), map(12,"Diciembre")
        );
    }

    private Map<String,Object> map(int valor, String nombre){
        return Map.of("valor", valor, "nombre", nombre);
    }

}
