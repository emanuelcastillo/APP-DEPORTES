package com.deportes.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "items_orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    @JsonIgnore
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;

    // Método para calcular subtotal
    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
}