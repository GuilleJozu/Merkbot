package com.ls.merkbot;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientesService {
    private ClientesRepository repClientes;

    public ClientesService(ClientesRepository repClientes){
        this.repClientes = repClientes;
    }

    public List<Clientes> listarClientes(){
       return repClientes.findActivas();
    }


    public Clientes buscarPorId(Long id) {
        return repClientes.findById(id).orElse(null);
    }

    public Clientes guardarClientes(Clientes cliente) {
        cliente.setFechaHora(LocalDateTime.now());
        return repClientes.save(cliente);
    }

    public Clientes actualizarClientes(Clientes clientes) {
        Clientes existente = repClientes.findById(clientes.getId_clientes())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existente.setNombre(clientes.getNombre());
        existente.setActivo(clientes.getActivo());
        existente.setDireccion(clientes.getDireccion());
        existente.setRfc(clientes.getRfc());
        existente.setTelefono(clientes.getTelefono());

        existente.setUpdatedAt(LocalDateTime.now());

        return repClientes.save(existente);
    }

    public void eliminarLogico(long id) {
        Clientes cliente = buscarPorId(id);
        if (cliente != null) {
            cliente.setActivo(false);
            cliente.setDeletedAt(LocalDateTime.now());
            repClientes.save(cliente);
        }
    }
}
