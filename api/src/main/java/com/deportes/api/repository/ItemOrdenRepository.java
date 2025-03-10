package com.deportes.api.repository;


import com.deportes.api.entity.ItemOrden;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrdenRepository extends JpaRepository<ItemOrden, Long> {

    List<ItemOrden> findByOrdenId(Long ordenId);

    @Query("SELECT i FROM ItemOrden i WHERE i.producto.id = :productoId")
    List<ItemOrden> findByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT SUM(i.cantidad) FROM ItemOrden i WHERE i.producto.id = :productoId")
    Long countTotalVentasByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT i.producto.id, SUM(i.cantidad) as total FROM ItemOrden i GROUP BY i.producto.id ORDER BY total DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);
}