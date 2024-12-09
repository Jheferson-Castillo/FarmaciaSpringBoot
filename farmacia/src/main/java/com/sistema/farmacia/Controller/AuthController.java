package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Muestra el formulario de inicio de sesión
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // Vista login.html
    }

    // Procesa el inicio de sesión
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        // Busca al usuario por correo
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(email);

        // Verifica si el usuario existe y si la contraseña es correcta
        if (usuario != null && usuario.getContrasena().equals(password)) {
            // Guarda el usuario en la sesión
            session.setAttribute("usuario", usuario);
            return "redirect:/dashboard"; // Redirige al dashboard
        }

        // Si las credenciales son incorrectas, regresa al formulario con un mensaje de error
        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    // Cierra la sesión del usuario
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Invalida la sesión
        return "redirect:/auth/login"; // Redirige al login
    }
}
