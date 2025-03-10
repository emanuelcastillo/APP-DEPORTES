package com.deportes.api.repository;

import com.deportes.api.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.producto WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdWithItems(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(i) FROM Carrito c JOIN c.items i WHERE c.usuario.id = :usuarioId")
    Long countItemsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Carrito c JOIN c.items i WHERE c.usuario.id = :usuarioId AND i.producto.id = :productoId")
    boolean existsProductoInCarrito(@Param("usuarioId") Long usuarioId, @Param("productoId") Long productoId);
}

