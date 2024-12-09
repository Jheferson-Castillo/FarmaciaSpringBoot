package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Empleado;
import com.sistema.farmacia.Service.EmpleadoService;
import com.sistema.farmacia.Service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;

    public EmpleadoController(EmpleadoService empleadoService, UsuarioService usuarioService) {
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.listarEmpleados());
        return "empleados/lista"; // Vista lista.html
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "empleados/formulario"; // Vista formulario.html
    }

    @PostMapping
    public String registrarEmpleado(@ModelAttribute Empleado empleado) {
        empleadoService.registrarEmpleado(empleado);
        return "redirect:/empleados";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return "redirect:/empleados";
    }
}