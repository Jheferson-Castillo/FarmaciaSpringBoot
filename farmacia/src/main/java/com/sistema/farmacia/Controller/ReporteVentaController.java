package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Model.Venta;
import com.sistema.farmacia.Service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes/ventas")
public class ReporteVentaController {

    private final VentaService ventaService;

    public ReporteVentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String mostrarReporteVentas(Model model, HttpSession session) {
        // Recuperar el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agregar el usuario al modelo
        model.addAttribute("usuario", usuario);

        // Obtener todas las ventas
        List<Venta> ventas = ventaService.listarVentas();

        // Formatear la fecha para cada venta
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (Venta venta : ventas) {
            venta.setFormattedFecha(venta.getFecha().format(formatter));
        }

        model.addAttribute("ventas", ventas);
        return "reporte-ventas"; // Renderiza la vista `reporte-ventas.html`
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<ByteArrayResource> generarPdf(@PathVariable Integer id, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return ResponseEntity.status(401).build(); // Retorna un código 401 si no está autenticado
        }

        // Generar el PDF como byte array
        byte[] pdfBytes = ventaService.generarPdf(id);

        // Configurar los encabezados HTTP para la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-venta-" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(pdfBytes));
    }
}
