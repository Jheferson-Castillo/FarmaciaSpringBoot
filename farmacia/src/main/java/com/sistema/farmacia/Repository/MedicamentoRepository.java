package com.sistema.farmacia.Repository;

import com.sistema.farmacia.Model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {
    Medicamento findByCodigo(String codigo);

    // Método para buscar medicamentos por laboratorio ID
    List<Medicamento> findByLaboratorioId(Integer laboratorioId);
}