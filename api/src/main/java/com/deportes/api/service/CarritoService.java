package com.deportes.api.service;

import com.deportes.api.entity.Carrito;
import com.deportes.api.entity.ItemCarrito;
import com.deportes.api.entity.Producto;
import com.deportes.api.entity.Usuario;
import com.deportes.api.repository.CarritoRepository;
import com.deportes.api.repository.ItemCarritoRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface CarritoService {
    Carrito findCarritoByUsuarioId(Long usuarioId);

    Carrito findCarritoWithItemsByUsuarioId(Long usuarioId);


    ItemCarrito agregarProductoAlCarrito(Long usuarioId, Long productoId, Integer cantidad);


    void actualizarCantidadProducto(Long usuarioId, Long productoId, Integer cantidad);


    void eliminarProductoDelCarrito(Long usuarioId, Long productoId);


    void vaciarCarrito(Long usuarioId);

    List<ItemCarrito> getItemsCarrito(Long usuarioId);

    BigDecimal calcularTotalCarrito(Long usuarioId);

    Long contarItemsCarrito(Long usuarioId);

    void addProduct(Long productId, Long userId, Integer quantity);
}
