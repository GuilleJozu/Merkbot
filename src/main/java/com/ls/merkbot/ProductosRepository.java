package com.ls.merkbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long> {

    @Query("SELECT p FROM Productos p WHERE p.competencia = false")
    List<Productos> findCompetencia();

    @Query("SELECT p FROM Productos p WHERE p.activo = true")
    List<Productos> findActivas();
}