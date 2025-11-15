package com.ls.merkbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository <Usuarios, Long> {
    Optional<Usuarios> findByUsuario(String usuario);

    @Query("SELECT u FROM Usuarios u WHERE u.activo = true")
    List<Usuarios> findActivas();

}
