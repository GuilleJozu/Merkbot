package com.ls.merkbot;

import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository repoUsuario;
    public UsuarioService(UsuarioRepository repoUsuario) {
        this.repoUsuario = repoUsuario;
    }

    public List<Usuarios> listarUsuarios(){
        return repoUsuario.findActivas();
    }

    @Override
    public UserDetails loadUserByUsername(String Username) throws UsernameNotFoundException {
        Usuarios usuario = repoUsuario.findByUsuario(Username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado:" + Username));

        String roleName = "ROLE_" + usuario.getRol().getNombre().toUpperCase().replace(" ", "_");
        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getContraseña())
                .authorities(roleName)
                .build();


    }

    public Usuarios buscarPorId(Long id) {
        return repoUsuario.findById(id).orElse(null);
    }


    public Usuarios guardarUsuarios(Usuarios usuario) {
        usuario.setFechaHora(LocalDateTime.now());
        usuario.setActivo(true);
        return repoUsuario.save(usuario);
    }


    public Usuarios actualizarUsuario(Usuarios usuarioActualizado) {

        Usuarios existente = repoUsuario.findById(usuarioActualizado.getId_user())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        existente.setNombre(usuarioActualizado.getNombre());
        existente.setApellido(usuarioActualizado.getApellido());
        existente.setUsuario(usuarioActualizado.getUsuario());
        existente.setContraseña(usuarioActualizado.getContraseña());
        existente.setRol(usuarioActualizado.getRol());
        if (usuarioActualizado.getActivo() != null) {
            existente.setActivo(usuarioActualizado.getActivo());
        }


        existente.setUpdatedAt(LocalDateTime.now());

        return repoUsuario.save(existente);
    }

    public void eliminarLogico(long id) {
        Usuarios usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(false);
            usuario.setDeletedAt(LocalDateTime.now());
            repoUsuario.save(usuario);
        }
    }




}
