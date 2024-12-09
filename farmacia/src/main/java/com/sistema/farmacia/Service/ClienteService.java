package com.sistema.farmacia.Service;

import com.sistema.farmacia.Model.Cliente;
import com.sistema.farmacia.Repository.ClienteRepository;
import com.sistema.farmacia.Repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public ClienteService(ClienteRepository clienteRepository, VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente registrarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(Integer id) {
        // Verificar si el cliente tiene ventas asociadas
        if (!ventaRepository.findByClienteId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el cliente porque tiene ventas asociadas.");
        }
        clienteRepository.deleteById(id);
    }
}