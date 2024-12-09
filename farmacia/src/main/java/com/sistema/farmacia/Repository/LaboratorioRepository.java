package com.sistema.farmacia.Repository;

import com.sistema.farmacia.Model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> { // Cambiado Long a Integer

}
