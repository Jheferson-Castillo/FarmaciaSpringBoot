package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Cliente;
import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarYRegistrarClientes(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        // Recuperar el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agregar el usuario y el mensaje de error al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("error", error);

        // Listar clientes y preparar formulario
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        model.addAttribute("cliente", new Cliente()); // Para el formulario de registro

        return "clientes"; // Vista clientes.html
    }

    @PostMapping
    public String registrarCliente(@ModelAttribute Cliente cliente, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        clienteService.registrarCliente(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Cliente cliente = clienteService.obtenerClientePorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        model.addAttribute("usuario", usuario); // Agregar usuario al modelo
        model.addAttribute("cliente", cliente); // Para el formulario de edición
        model.addAttribute("clientes", clienteService.listarClientes()); // Para la lista

        return "clientes";
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Integer id, @ModelAttribute Cliente cliente, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        cliente.setId(id);
        clienteService.registrarCliente(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Integer id, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            clienteService.eliminarCliente(id);
        } catch (RuntimeException e) {
            // Redirigir con el mensaje de error
            return "redirect:/clientes?error=" + e.getMessage();
        }

        return "redirect:/clientes";
    }
}
