package com.sistema.farmacia.Service;

import com.sistema.farmacia.Model.Laboratorio;
import com.sistema.farmacia.Repository.LaboratorioRepository;
import com.sistema.farmacia.Repository.MedicamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;
    private final MedicamentoRepository medicamentoRepository;

    public LaboratorioService(LaboratorioRepository laboratorioRepository, MedicamentoRepository medicamentoRepository) {
        this.laboratorioRepository = laboratorioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    // Listar todos los laboratorios
    public List<Laboratorio> listarLaboratorios() {
        return laboratorioRepository.findAll();
    }

    // Obtener un laboratorio por su ID
    public Laboratorio buscarPorId(Integer id) {
        return laboratorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratorio con ID " + id + " no encontrado."));
    }

    // Registrar un nuevo laboratorio
    public Laboratorio registrarLaboratorio(Laboratorio laboratorio) {
        return laboratorioRepository.save(laboratorio);
    }

    @Transactional
    public void eliminarLaboratorio(Integer id) {
        // Verificar si hay medicamentos asociados al laboratorio
        if (!medicamentoRepository.findByLaboratorioId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar el laboratorio porque tiene medicamentos asociados.");
        }
        laboratorioRepository.deleteById(id);
    }
}
