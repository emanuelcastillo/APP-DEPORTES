package com.deportes.api.repository;

import com.deportes.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.recuperacionToken = :token AND u.recuperacionTokenExpiracion >= CURRENT_DATE")
    Optional<Usuario> findByValidRecuperacionToken(@Param("token") String token);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.carrito c LEFT JOIN FETCH c.items WHERE u.id = :id")
    Optional<Usuario> findByIdWithCarrito(@Param("id") Long id);
}
