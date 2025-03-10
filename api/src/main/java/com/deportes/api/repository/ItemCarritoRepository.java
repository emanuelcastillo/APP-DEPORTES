package com.deportes.api.repository;

import com.deportes.api.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarritoId(Long carritoId);

    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ItemCarrito i WHERE i.carrito.id = :carritoId AND i.producto.id = :productoId")
    void deleteByCarritoIdAndProductoId(@Param("carritoId") Long carritoId, @Param("productoId") Long productoId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ItemCarrito i WHERE i.carrito.id = :carritoId")
    void deleteAllByCarritoId(@Param("carritoId") Long carritoId);

    @Query("SELECT SUM(i.cantidad) FROM ItemCarrito i WHERE i.carrito.id = :carritoId")
    Long countTotalItemsInCarrito(@Param("carritoId") Long carritoId);
}