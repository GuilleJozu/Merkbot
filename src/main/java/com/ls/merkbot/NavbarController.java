package com.ls.merkbot;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavbarController {
    @ModelAttribute ("currentPage")
    public String currentPage(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.equals("/")) return "home";
        if (uri.startsWith("/registros")) return "registros";
        if (uri.startsWith("/usuarios")) return "usuarios";
        if (uri.startsWith("/clientes")) return "clientes";
        if (uri.startsWith("/productos")) return "productos";
        if (uri.startsWith("/roles")) return "roles";

        return "";
    }

    @ModelAttribute("usuario")
    public String usuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "Invitado";
    }


}
