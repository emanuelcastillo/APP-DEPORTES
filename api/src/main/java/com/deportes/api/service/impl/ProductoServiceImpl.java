package com.deportes.api.service.impl;

import com.deportes.api.entity.Producto;
import com.deportes.api.repository.ProductoRepository;
import com.deportes.api.service.ProductoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;

    @Transactional
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Transactional
    public Page<Producto> findAllAvailable(Pageable pageable) {
        return productoRepository.findAllAvailable(pageable);
    }

    @Transactional
    public Page<Producto> searchByDescripcion(String descripcion, Pageable pageable) {
        return productoRepository.searchByDescripcion(descripcion, pageable);
    }

    @Transactional
    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto update(Long id, Producto productoDetails) {
        Producto producto = findById(id);

        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setMonto(productoDetails.getMonto());
        producto.setCantidadDisponible(productoDetails.getCantidadDisponible());
        producto.setRutaImagen(productoDetails.getRutaImagen());

        return productoRepository.save(producto);
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = findById(id);
        productoRepository.delete(producto);
    }

    @Transactional
    public List<Producto> findProductosWithLowStock(Integer cantidad) {
        return productoRepository.findProductosWithLowStock(cantidad);
    }

    @Transactional
    public Long countOutOfStockProducts() {
        return productoRepository.countOutOfStockProducts();
    }

    @Transactional
    public void actualizarStock(Long id, Integer cantidad) {
        Producto producto = findById(id);
        producto.setCantidadDisponible(cantidad);
        productoRepository.save(producto);
    }

    @Transactional
    public boolean verificarStock(Long id, Integer cantidad) {
        Producto producto = findById(id);
        return producto.tieneStock(cantidad);
    }

    @Transactional
    public void reducirStock(Long id, Integer cantidad) {
        Producto producto = findById(id);
        producto.reducirStock(cantidad);
        productoRepository.save(producto);
    }

    @Override
    public List<Producto> findByNombreCategoria(String nombreCategoria) {
        return List.of();
    }

    @Override
    public Page<Producto> findByCategoria(String category, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findByCategoriaNombre(category, pageable);
    }

}
