package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping
    public String mostrarDashboard(HttpSession session, Model model) {
        logger.info("Accediendo a /dashboard");

        // Recupera el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            logger.warn("Usuario no encontrado en la sesión. Redirigiendo a /auth/login.");
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agrega el usuario al modelo para que Thymeleaf pueda acceder a él
        model.addAttribute("usuario", usuario);

        logger.info("Usuario encontrado: {}, Rol: {}", usuario.getNombreUsuario(), usuario.getRol().name());

        // Retorna siempre la misma vista
        return "dashboard";
    }
}
