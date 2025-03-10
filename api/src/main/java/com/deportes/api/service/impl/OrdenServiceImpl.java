package com.deportes.api.service.impl;

import com.deportes.api.entity.ItemCarrito;
import com.deportes.api.entity.ItemOrden;
import com.deportes.api.entity.Orden;
import com.deportes.api.entity.Producto;
import com.deportes.api.entity.Usuario;
import com.deportes.api.exception.CustomValidationException;
import com.deportes.api.repository.ItemOrdenRepository;
import com.deportes.api.repository.OrdenRepository;
import com.deportes.api.service.CarritoService;
import com.deportes.api.service.OrdenService;
import com.deportes.api.service.ProductoService;
import com.deportes.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {
    private final OrdenRepository ordenRepository;
    private final ItemOrdenRepository itemOrdenRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final UserService usuarioService;

    @Transactional
    public List<Orden> findAll() {
        return ordenRepository.findAll();
    }

    @Transactional
    public Page<Orden> findByUsuarioId(Long usuarioId, Pageable pageable) {
        return ordenRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Transactional
    public Orden findById(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
    }

    @Transactional
    public Orden findByIdWithItems(Long id) {
        return ordenRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
    }

    @Transactional
    public Orden findByNumeroOrden(String numeroOrden) {
        return ordenRepository.findByNumeroOrden(numeroOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con número: " + numeroOrden));
    }

    @Transactional
    public Orden crearOrdenDesdeCarrito(Long usuarioId, String direccionEnvio) {
        Usuario usuario = usuarioService.findById(usuarioId);
        List<ItemCarrito> itemsCarrito = carritoService.getItemsCarrito(usuarioId);

        if (itemsCarrito.isEmpty()) {
            throw new IllegalStateException("No se puede crear una orden con un carrito vacío");
        }

        // Verificar stock para todos los productos
        for (ItemCarrito item : itemsCarrito) {
            Producto producto = item.getProducto();
            if (!producto.tieneStock(item.getCantidad())) {
                throw new CustomValidationException(
                        "Stock insuficiente para el producto: " + producto.getDescripcion());
            }
        }

        // Crear nueva orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setTotal(BigDecimal.ZERO);
        orden.setNumeroOrden(generarNumeroOrden());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setDireccionEnvio(direccionEnvio != null ? direccionEnvio : usuario.getDireccionEnvio());
        orden.setEstado(Orden.EstadoOrden.PENDIENTE);

        // Guardar orden para obtener ID
        orden = ordenRepository.save(orden);

        // Transferir items del carrito a la orden y reducir stock
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito itemCarrito : itemsCarrito) {
            Producto producto = itemCarrito.getProducto();

            // Crear ítem de orden
            ItemOrden itemOrden = new ItemOrden();
            itemOrden.setOrden(orden);
            itemOrden.setProducto(producto);
            itemOrden.setCantidad(itemCarrito.getCantidad());
            itemOrden.setPrecioUnitario(itemCarrito.getPrecioUnitario());

            itemOrdenRepository.save(itemOrden);

            // Reducir stock
            productoService.reducirStock(producto.getId(), itemCarrito.getCantidad());

            // Acumular total
            total = total.add(itemOrden.calcularSubtotal());
        }

        // Actualizar total de la orden
        orden.setTotal(total);
        orden = ordenRepository.save(orden);

        // Vaciar carrito
        carritoService.vaciarCarrito(usuarioId);

        return orden;
    }

    @Transactional
    public Orden actualizarEstadoOrden(Long id, Orden.EstadoOrden nuevoEstado) {
        Orden orden = findById(id);
        orden.setEstado(nuevoEstado);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden actualizarDireccionEnvio(Long id, String nuevaDireccion) {
        Orden orden = findById(id);

        // Solo se puede cambiar la dirección si la orden está pendiente
        if (orden.getEstado() != Orden.EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("No se puede modificar la dirección de una orden que ya está en proceso");
        }

        orden.setDireccionEnvio(nuevaDireccion);
        return ordenRepository.save(orden);
    }

    @Transactional
    public List<Orden> findByEstado(Orden.EstadoOrden estado) {
        return ordenRepository.findByEstado(estado);
    }

    @Transactional
    public List<Orden> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ordenRepository.findByDateRange(startDate, endDate);
    }

    @Transactional
    public Long countByUsuarioId(Long usuarioId) {
        return ordenRepository.countByUsuarioId(usuarioId);
    }

    private String generarNumeroOrden() {
        // Formato: ORD-[año][mes][dia]-[UUID aleatorio corto]
        LocalDateTime now = LocalDateTime.now();
        String fecha = String.format("%d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "ORD-" + fecha + "-" + randomPart;
    }
}
