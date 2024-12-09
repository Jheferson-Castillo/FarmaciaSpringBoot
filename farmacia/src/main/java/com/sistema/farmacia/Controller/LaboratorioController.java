package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Laboratorio;
import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Service.LaboratorioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/laboratorios")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    public LaboratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @GetMapping
    public String listarLaboratorios(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        // Recuperar el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agregar el usuario y el mensaje de error al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("error", error);

        // Obtener lista de laboratorios y pasarla al modelo
        List<Laboratorio> laboratorios = laboratorioService.listarLaboratorios();
        model.addAttribute("laboratorios", laboratorios);

        // Agregar un laboratorio vacío para el formulario
        model.addAttribute("laboratorio", new Laboratorio());

        return "laboratorio"; // Vista única para lista y formulario
    }

    @PostMapping("/guardar")
    public String registrarLaboratorio(@ModelAttribute Laboratorio laboratorio, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        laboratorioService.registrarLaboratorio(laboratorio);
        return "redirect:/laboratorios";
    }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        // Obtener laboratorio usando el servicio
        Laboratorio laboratorio = laboratorioService.buscarPorId(id);

        // Agregar datos al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("laboratorio", laboratorio); // Para el formulario de edición
        model.addAttribute("laboratorios", laboratorioService.listarLaboratorios()); // Para la lista

        return "laboratorio"; // Usamos la misma vista para registro y edición
    }

    @PostMapping("/editar/{id}")
    public String actualizarLaboratorio(@PathVariable Integer id, @ModelAttribute Laboratorio laboratorio, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        // Actualizar el laboratorio con el ID especificado
        laboratorio.setId(id);
        laboratorioService.registrarLaboratorio(laboratorio);

        return "redirect:/laboratorios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLaboratorio(@PathVariable Integer id, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            laboratorioService.eliminarLaboratorio(id);
        } catch (RuntimeException e) {
            return "redirect:/laboratorios?error=" + e.getMessage();
        }

        return "redirect:/laboratorios";
    }
}