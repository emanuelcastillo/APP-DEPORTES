package com.deportes.api.repository;

import com.deportes.api.entity.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @Query("SELECT o FROM Orden o WHERE o.usuario.id = :usuarioId")
    Page<Orden> findByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);

    Optional<Orden> findByNumeroOrden(String numeroOrden);

    @Query("SELECT o FROM Orden o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.producto WHERE o.id = :ordenId")
    Optional<Orden> findByIdWithItems(@Param("ordenId") Long ordenId);

    @Query("SELECT o FROM Orden o WHERE o.estado = :estado")
    List<Orden> findByEstado(@Param("estado") Orden.EstadoOrden estado);

    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion BETWEEN :startDate AND :endDate")
    List<Orden> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}