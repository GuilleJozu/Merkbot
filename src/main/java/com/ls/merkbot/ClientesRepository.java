package com.ls.merkbot;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientesRepository extends JpaRepository <Clientes,Long> {

    @Query("SELECT c FROM Clientes c WHERE c.activo = true")
    List<Clientes> findActivas();


}
