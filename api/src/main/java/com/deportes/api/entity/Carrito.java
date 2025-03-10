package com.deportes.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    // Métodos para manipular items del carrito
    public void agregarItem(Producto producto, int cantidad) {
        // Verificar si el producto ya está en el carrito
        for (ItemCarrito item : items) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }

        // Si el producto no está en el carrito, añadirlo
        ItemCarrito nuevoItem = new ItemCarrito();
        nuevoItem.setCarrito(this);
        nuevoItem.setProducto(producto);
        nuevoItem.setCantidad(cantidad);
        nuevoItem.setPrecioUnitario(producto.getMonto());
        items.add(nuevoItem);
    }

    public void eliminarItem(Long productoId) {
        items.removeIf(item -> item.getProducto().getId().equals(productoId));
    }

    public void actualizarCantidadItem(Long productoId, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId().equals(productoId)) {
                item.setCantidad(cantidad);
                return;
            }
        }
    }

    public BigDecimal calcularTotal() {
        return items.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vaciar() {
        items.clear();
    }
}