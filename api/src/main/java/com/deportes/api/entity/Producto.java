package com.deportes.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 0, message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible debe ser positiva")
    private Integer cantidadDisponible;

    @NotBlank(message = "La ruta de la imagen es obligatoria")
    private String rutaImagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Método para verificar disponibilidad
    public boolean tieneStock(int cantidad) {
        return this.cantidadDisponible >= cantidad;
    }

    // Método para reducir stock después de una compra
    public void reducirStock(int cantidad) {
        if (tieneStock(cantidad)) {
            this.cantidadDisponible -= cantidad;
        } else {
            throw new IllegalArgumentException("No hay suficiente stock disponible");
        }
    }
}