package com.deportes.api.service;



import com.deportes.api.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductoService {
    List<Producto> findAll();
    Page<Producto> findAllAvailable(Pageable pageable);
    Page<Producto> searchByDescripcion(String descripcion, Pageable pageable);
    Producto findById(Long id);
    Producto save(Producto producto);
    Producto update(Long id, Producto productoDetails);
    void delete(Long id);
    List<Producto> findProductosWithLowStock(Integer cantidad);
    Long countOutOfStockProducts();
    void actualizarStock(Long id, Integer cantidad);
    boolean verificarStock(Long id, Integer cantidad);
    void reducirStock(Long id, Integer cantidad);
    List<Producto> findByNombreCategoria(String nombreCategoria);

    Page<Producto> findByCategoria(String category, Integer page, Integer size);
}