package com.deportes.api.service.impl;

import com.deportes.api.entity.Carrito;
import com.deportes.api.entity.ItemCarrito;
import com.deportes.api.entity.Producto;
import com.deportes.api.entity.Usuario;
import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.repository.CarritoRepository;
import com.deportes.api.repository.ItemCarritoRepository;
import com.deportes.api.service.CarritoService;
import com.deportes.api.service.ProductoService;
import com.deportes.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoService productoService;
    private final UserService usuarioService;

    @Transactional
    public Carrito findCarritoByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario con id: " + usuarioId));
    }

    @Transactional
    public Carrito findCarritoWithItemsByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario con id: " + usuarioId));
    }

    @Transactional
    public ItemCarrito agregarProductoAlCarrito(Long usuarioId, Long productoId, Integer cantidad) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioService.findById(usuarioId);
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepository.save(nuevoCarrito);
                });

        Producto producto = productoService.findById(productoId);

        // Verificar stock
        if (!producto.tieneStock(cantidad)) {
            throw new CustomValidationException("No hay suficiente stock disponible para el producto: " + producto.getDescripcion());
        }

        // Buscar si el producto ya está en el carrito
        ItemCarrito itemCarrito = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElse(null);

        if (itemCarrito != null) {
            // Actualizar cantidad si ya existe
            int nuevaCantidad = itemCarrito.getCantidad() + cantidad;

            // Verificar stock para la nueva cantidad
            if (!producto.tieneStock(nuevaCantidad)) {
                throw new CustomValidationException("No hay suficiente stock disponible para la cantidad solicitada");
            }

            itemCarrito.setCantidad(nuevaCantidad);
        } else {
            // Crear nuevo item si no existe
            itemCarrito = new ItemCarrito();
            itemCarrito.setCarrito(carrito);
            itemCarrito.setProducto(producto);
            itemCarrito.setCantidad(cantidad);
            itemCarrito.setPrecioUnitario(producto.getMonto());
        }

        return itemCarritoRepository.save(itemCarrito);
    }

    @Transactional
    public void actualizarCantidadProducto(Long usuarioId, Long productoId, Integer cantidad) {
        Carrito carrito = findCarritoByUsuarioId(usuarioId);

        ItemCarrito itemCarrito = itemCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en el carrito"));

        Producto producto = productoService.findById(productoId);

        // Verificar stock
        if (!producto.tieneStock(cantidad)) {
            throw new CustomValidationException("No hay suficiente stock disponible para la cantidad solicitada");
        }

        if (cantidad <= 0) {
            // Eliminar el producto si la cantidad es 0 o negativa
            eliminarProductoDelCarrito(usuarioId, productoId);
        } else {
            itemCarrito.setCantidad(cantidad);
            itemCarritoRepository.save(itemCarrito);
        }
    }

    @Transactional
    public void eliminarProductoDelCarrito(Long usuarioId, Long productoId) {
        Carrito carrito = findCarritoByUsuarioId(usuarioId);
        itemCarritoRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = findCarritoByUsuarioId(usuarioId);
        itemCarritoRepository.deleteAllByCarritoId(carrito.getId());
    }

    @Transactional
    public List<ItemCarrito> getItemsCarrito(Long usuarioId) {
        Carrito carrito = findCarritoByUsuarioId(usuarioId);
        return itemCarritoRepository.findByCarritoId(carrito.getId());
    }

    @Transactional
    public BigDecimal calcularTotalCarrito(Long usuarioId) {
        List<ItemCarrito> items = getItemsCarrito(usuarioId);

        return items.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Long contarItemsCarrito(Long usuarioId) {
        Carrito carrito = findCarritoByUsuarioId(usuarioId);
        return itemCarritoRepository.countTotalItemsInCarrito(carrito.getId());
    }

    @Override
    public void addProduct(Long productId, Long userId, Integer quantity) {
        agregarProductoAlCarrito(userId, productId, quantity);

    }
}
