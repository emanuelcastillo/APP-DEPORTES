package com.deportes.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    @Column(unique = true)
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    // Token para recuperación de contraseña
    private String recuperacionToken;
    private LocalDate recuperacionTokenExpiracion;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Carrito carrito;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orden> ordenes = new ArrayList<>();

    // Método para verificar si el usuario es mayor de 18 años
    @AssertTrue(message = "Debes ser mayor de 18 años para registrarte")
    private boolean isMayorDeEdad() {
        if (fechaNacimiento == null) {
            return false;
        }
        return fechaNacimiento.plusYears(18).isBefore(LocalDate.now()) ||
                fechaNacimiento.plusYears(18).isEqual(LocalDate.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "USER");
    }

    @Override
    public String getUsername() {
        return email;
    }
}