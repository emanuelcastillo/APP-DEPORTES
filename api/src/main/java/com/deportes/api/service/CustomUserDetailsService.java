package com.deportes.api.service;

import com.deportes.api.entity.Usuario;
import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UsuarioRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new CustomValidationException("Email o contraseña incorrectos")
        );
    }
}