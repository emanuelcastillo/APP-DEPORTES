package com.deportes.api.service.impl;

import com.deportes.api.config.JwtProvider;
import com.deportes.api.entity.Carrito;
import com.deportes.api.entity.Usuario;
import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.records.LoginRequest;
import com.deportes.api.records.RegisterRequest;
import com.deportes.api.repository.UsuarioRepository;
import com.deportes.api.service.AuthService;
import com.deportes.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository userService;
    private final JwtProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            if (authentication == null) throw new CustomValidationException("Contraseña y/o usuario incorrecto");
            else {
                Optional<Usuario> user = userService.findByEmail(request.email());
                if (user.isEmpty()) throw new CustomValidationException("Contraseña y/o usuario incorrecto");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return jwtTokenProvider.generateToken(authentication);
            }
        } catch (Exception e) {
            throw new CustomValidationException("Exception: "+ e.getMessage());
        }
    }

    @Transactional
    @Override
    public void register(RegisterRequest request) {
            Usuario user = new Usuario();
            user.setEmail(request.email());
            user.setNombre(request.nombre());
            user.setRecuperacionToken(UUID.randomUUID().toString());
            user.setRecuperacionTokenExpiracion(LocalDate.now().plusDays(365));
            user.setApellido(request.apellido());
            user.setFechaNacimiento(request.fechaNacimiento());
            user.setDireccionEnvio(request.direccionEnvio());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setCarrito(Carrito.builder().items(List.of()).build());
            userService.save(user);
    }
}