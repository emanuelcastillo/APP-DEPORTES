package com.deportes.api.service;

import com.deportes.api.entity.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrdenService {
    List<Orden> findAll();

    Page<Orden> findByUsuarioId(Long usuarioId, Pageable pageable);

    Orden findById(Long id);

    Orden findByIdWithItems(Long id);

    Orden findByNumeroOrden(String numeroOrden);

    Orden crearOrdenDesdeCarrito(Long usuarioId, String direccionEnvio);

    Orden actualizarEstadoOrden(Long id, Orden.EstadoOrden nuevoEstado);

    Orden actualizarDireccionEnvio(Long id, String nuevaDireccion);

    List<Orden> findByEstado(Orden.EstadoOrden estado);

    List<Orden> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countByUsuarioId(Long usuarioId);

    private String generarNumeroOrden() {
        // Formato: ORD-[año][mes][dia]-[UUID aleatorio corto]
        LocalDateTime now = LocalDateTime.now();
        String fecha = String.format("%d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "ORD-" + fecha + "-" + randomPart;
    }
}
