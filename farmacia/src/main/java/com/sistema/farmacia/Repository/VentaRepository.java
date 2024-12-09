package com.sistema.farmacia.Repository;

import com.sistema.farmacia.Model.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente JOIN FETCH v.medicamento")
    List<Venta> findAllWithRelations();

    // Método para buscar ventas por cliente ID
    List<Venta> findByClienteId(Integer clienteId);

    // Método para buscar ventas por medicamento ID
    List<Venta> findByMedicamentoId(Integer medicamentoId);
}
