package com.deportes.api.service.impl;

import com.deportes.api.entity.Carrito;
import com.deportes.api.entity.Usuario;
import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.records.UserRequest;
import com.deportes.api.repository.CarritoRepository;
import com.deportes.api.repository.UsuarioRepository;
import com.deportes.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Transactional
    public Usuario findByIdWithCarrito(Long id) {
        return usuarioRepository.findByIdWithCarrito(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public Usuario register(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new CustomValidationException("El email ya está registrado: " + usuario.getEmail());
        }

        // Verificar edad mínima
        if (!usuario.getFechaNacimiento().isBefore(LocalDate.now().minusYears(18))) {
            throw new CustomValidationException("Debes ser mayor de 18 años para registrarte");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar usuario
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Crear carrito vacío para el usuario
        Carrito carrito = new Carrito();
        carrito.setUsuario(savedUsuario);
        carritoRepository.save(carrito);

        return savedUsuario;
    }

    @Transactional
    public Usuario update(Long id, Usuario usuarioDetails) {
        Usuario usuario = findById(id);

        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setApellido(usuarioDetails.getApellido());
        usuario.setDireccionEnvio(usuarioDetails.getDireccionEnvio());

        // No actualizar email si ya existe para otro usuario
        if (!usuario.getEmail().equals(usuarioDetails.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDetails.getEmail())) {
            throw new CustomValidationException("El email ya está registrado: " + usuarioDetails.getEmail());
        }

        usuario.setEmail(usuarioDetails.getEmail());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = findById(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void cambiarPassword(Long id, String currentPassword, String newPassword) {
        Usuario usuario = findById(id);

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public String generarTokenRecuperacion(String email) {
        Usuario usuario = findByEmail(email);

        String token = UUID.randomUUID().toString();
        usuario.setRecuperacionToken(token);
        // Token válido por 24 horas
        usuario.setRecuperacionTokenExpiracion(LocalDate.now().plusDays(1));

        usuarioRepository.save(usuario);

        return token;
    }

    @Transactional
    public void recuperarPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByValidRecuperacionToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de recuperación inválido o expirado"));

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setRecuperacionToken(null);
        usuario.setRecuperacionTokenExpiracion(null);

        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Override
    public void updateMe(UserRequest userRequest) {
        Usuario usuario = getMe();

        usuario.setNombre(userRequest.nombre());
        usuario.setApellido(userRequest.apellido());
        usuario.setDireccionEnvio(userRequest.direccionEnvio());

        // No actualizar email si ya existe para otro usuario
        if (!usuario.getEmail().equals(userRequest.email()) &&
                usuarioRepository.existsByEmail(userRequest.email())) {
            throw new CustomValidationException("El email ya está registrado: " + userRequest.email());
        }

        usuario.setEmail(userRequest.email());

        usuarioRepository.save(usuario);
    }
}
