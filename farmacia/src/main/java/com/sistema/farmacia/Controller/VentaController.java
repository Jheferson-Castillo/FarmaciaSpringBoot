package com.sistema.farmacia.Controller;

import com.sistema.farmacia.Model.Cliente;
import com.sistema.farmacia.Model.Venta;
import com.sistema.farmacia.Model.Medicamento;
import com.sistema.farmacia.Model.Usuario;
import com.sistema.farmacia.Service.ClienteService;
import com.sistema.farmacia.Service.VentaService;
import com.sistema.farmacia.Service.MedicamentoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/ventas")
@SessionAttributes("carrito")
public class VentaController {

    private final VentaService ventaService;
    private final MedicamentoService medicamentoService;
    private final ClienteService clienteService;

    public VentaController(VentaService ventaService, MedicamentoService medicamentoService, ClienteService clienteService) {
        this.ventaService = ventaService;
        this.medicamentoService = medicamentoService;
        this.clienteService = clienteService;
    }

    @ModelAttribute("carrito")
    public List<Venta> inicializarCarrito() {
        return new ArrayList<>();
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(@RequestParam(value = "clienteId", required = false) Integer clienteId,
                                            Model model,
                                            HttpSession session,
                                            @ModelAttribute("carrito") List<Venta> carrito) {
        // Recupera el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login"; // Redirige al login si no hay usuario en sesión
        }

        // Agrega el usuario al modelo
        model.addAttribute("usuario", usuario);

        // Cargar clientes y medicamentos
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);

        if (clienteId == null && !clientes.isEmpty()) {
            clienteId = clientes.get(0).getId(); // Seleccionar el primer cliente por defecto
        }

        model.addAttribute("clienteId", clienteId);
        model.addAttribute("venta", new Venta());
        model.addAttribute("medicamentos", medicamentoService.listarMedicamentos());
        model.addAttribute("carrito", carrito);

        return "registrar_venta";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("medicamentoId") Integer medicamentoId,
                                   @RequestParam("cantidad") Integer cantidad,
                                   @RequestParam("clienteId") Integer clienteId,
                                   @ModelAttribute("carrito") List<Venta> carrito,
                                   Model model,
                                   HttpSession session) {
        Medicamento medicamento = medicamentoService.obtenerMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        // Validar el stock antes de añadir al carrito
        if (medicamento.getCantidad() < cantidad) {
            String mensajeError = "Stock insuficiente para el medicamento: " + medicamento.getNombre() +
                    ". Stock disponible: " + medicamento.getCantidad();
            model.addAttribute("error", mensajeError);
            prepararModeloParaVista(model, clienteId, carrito, session);
            return "registrar_venta";
        }

        Cliente cliente = clienteService.obtenerClientePorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Crear un nuevo item de venta y añadirlo al carrito
        Venta item = new Venta();
        item.setMedicamento(medicamento);
        item.setCantidad(cantidad);
        item.setPrecio(medicamento.getPrecio() * cantidad);
        item.setCliente(cliente);
        carrito.add(item);

        return "redirect:/ventas/nuevo?clienteId=" + clienteId;
    }


    @GetMapping("/carrito/eliminar/{medicamentoId}")
    public String eliminarDelCarrito(@PathVariable Integer medicamentoId,
                                     @ModelAttribute("carrito") List<Venta> carrito) {
        carrito.removeIf(item -> item.getMedicamento().getId().equals(medicamentoId));
        return "redirect:/ventas/nuevo";
    }

    @PostMapping
    public String registrarVenta(@RequestParam("clienteId") Integer clienteId,
                                 @ModelAttribute("carrito") List<Venta> carrito,
                                 Model model,
                                 HttpSession session) {
        if (carrito.isEmpty()) {
            model.addAttribute("error", "El carrito está vacío. No se puede realizar la venta.");
            prepararModeloParaVista(model, clienteId, carrito, session);
            return "registrar_venta";
        }

        Cliente cliente = clienteService.obtenerClientePorId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        try {
            for (Venta venta : carrito) {
                venta.setCliente(cliente);
                venta.setFecha(LocalDateTime.now()); // Asignar la fecha actual
                ventaService.registrarVenta(venta);
            }
        } catch (RuntimeException e) {
            // Agregar mensaje de error al modelo y preparar los datos para recargar la vista
            model.addAttribute("error", e.getMessage());
            prepararModeloParaVista(model, clienteId, carrito, session);
            return "registrar_venta";
        }

        carrito.clear(); // Vaciar el carrito después de la venta
        return "redirect:/ventas/nuevo"; // Redirigir a la misma vista para limpiar el formulario
    }

    private void prepararModeloParaVista(Model model, Integer clienteId, List<Venta> carrito, HttpSession session) {
        // Recuperar el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Asegurarse de agregar todos los datos necesarios para la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("carrito", carrito);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("medicamentos", medicamentoService.listarMedicamentos());
    }


}