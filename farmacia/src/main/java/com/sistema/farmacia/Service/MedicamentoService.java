package com.sistema.farmacia.Service;

import com.sistema.farmacia.Model.Medicamento;
import com.sistema.farmacia.Repository.MedicamentoRepository;
import com.sistema.farmacia.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final VentaRepository ventaRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository, VentaRepository ventaRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepository.findAll();
    }

    public Optional<Medicamento> obtenerMedicamentoPorId(Integer id) {
        return medicamentoRepository.findById(id);
    }

    public Medicamento obtenerMedicamentoPorCodigo(String codigo) {
        return medicamentoRepository.findByCodigo(codigo);
    }

    public Medicamento registrarMedicamento(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public void eliminarMedicamento(Integer id) {
        // Verificar si el medicamento tiene ventas asociadas
        if (!ventaRepository.findByMedicamentoId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el medicamento porque tiene ventas asociadas.");
        }
        medicamentoRepository.deleteById(id);
    }
}