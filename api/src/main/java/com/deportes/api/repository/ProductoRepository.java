package com.deportes.api.repository;

import com.deportes.api.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE p.cantidadDisponible > 0")
    Page<Producto> findAllAvailable(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')) AND p.cantidadDisponible > 0")
    Page<Producto> searchByDescripcion(@Param("descripcion") String descripcion, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.cantidadDisponible < :cantidad")
    List<Producto> findProductosWithLowStock(@Param("cantidad") Integer cantidad);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.cantidadDisponible = 0")
    Long countOutOfStockProducts();

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :nombreCategoria")
    Page<Producto> findByCategoriaNombre(String nombreCategoria, Pageable pageable);
}

