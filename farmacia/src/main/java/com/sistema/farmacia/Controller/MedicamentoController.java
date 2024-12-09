package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Laboratorio;
import com.sistema.farmacia.Model.Medicamento;
import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Service.MedicamentoService;
import com.sistema.farmacia.Service.LaboratorioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final LaboratorioService laboratorioService;

    public MedicamentoController(MedicamentoService medicamentoService, LaboratorioService laboratorioService) {
        this.medicamentoService = medicamentoService;
        this.laboratorioService = laboratorioService;
    }

    @GetMapping
    public String listarMedicamentos(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        // Recupera el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agrega el usuario y el mensaje de error al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("error", error);

        // Cargar medicamentos y laboratorios
        List<Medicamento> medicamentos = medicamentoService.listarMedicamentos();
        List<Laboratorio> laboratorios = laboratorioService.listarLaboratorios();

        // Agregar datos al modelo
        model.addAttribute("medicamentos", medicamentos); // Lista para la tabla
        model.addAttribute("laboratorios", laboratorios); // Lista para el formulario
        model.addAttribute("medicamento", new Medicamento()); // Objeto vacío para evitar errores

        return "medicamentos"; // Vista única
    }


    @PostMapping("/guardar")
    public String registrarMedicamento(@ModelAttribute Medicamento medicamento,
                                       @RequestParam("laboratorioId") Integer laboratorioId,
                                       HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        // Buscar el laboratorio por ID y guardar el medicamento
        Laboratorio laboratorio = laboratorioService.buscarPorId(laboratorioId);
        medicamento.setLaboratorio(laboratorio);
        medicamentoService.registrarMedicamento(medicamento);

        return "redirect:/medicamentos";
    }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Medicamento medicamento = medicamentoService.obtenerMedicamentoPorId(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        List<Laboratorio> laboratorios = laboratorioService.listarLaboratorios();

        // Agregar datos al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("medicamento", medicamento); // Para el formulario de edición
        model.addAttribute("laboratorios", laboratorios); // Para la lista de laboratorios
        model.addAttribute("medicamentos", medicamentoService.listarMedicamentos()); // Para la lista

        return "medicamentos";
    }

    @PostMapping("/editar/{id}")
    public String actualizarMedicamento(@PathVariable Integer id, @ModelAttribute Medicamento medicamento,
                                        @RequestParam("laboratorioId") Integer laboratorioId, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        // Buscar laboratorio y actualizar medicamento
        Laboratorio laboratorio = laboratorioService.buscarPorId(laboratorioId);
        medicamento.setLaboratorio(laboratorio);
        medicamento.setId(id);
        medicamentoService.registrarMedicamento(medicamento);

        return "redirect:/medicamentos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMedicamento(@PathVariable Integer id, HttpSession session) {
        // Validar sesión del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            medicamentoService.eliminarMedicamento(id);
        } catch (RuntimeException e) {
            return "redirect:/medicamentos?error=" + e.getMessage();
        }

        return "redirect:/medicamentos";
    }
}
