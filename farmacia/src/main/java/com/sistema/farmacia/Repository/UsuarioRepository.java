package com.sistema.farmacia.Repository;

import com.sistema.farmacia.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscar usuario por nombre de usuario o correo
    Usuario findByNombreUsuario(String nombreUsuario);
    Usuario findByCorreo(String correo);
}