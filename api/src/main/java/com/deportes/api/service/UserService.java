package com.deportes.api.service;
import com.deportes.api.entity.Usuario;
import com.deportes.api.records.UserRequest;


import java.util.List;


public interface UserService {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    Usuario findByIdWithCarrito(Long id);

    Usuario register(Usuario usuario);

    Usuario update(Long id, Usuario usuarioDetails);

    void delete(Long id);

    void cambiarPassword(Long id, String currentPassword, String newPassword);

    String generarTokenRecuperacion(String email) ;

void recuperarPassword(String token, String newPassword);

    Usuario getMe();

    void updateMe(UserRequest userRequest);
}
