package com.deportes.api.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El número de orden es obligatorio")
    private String numeroOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @NotNull(message = "La fecha de la orden es obligatoria")
    private LocalDateTime fechaCreacion;

    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ItemOrden> items = new ArrayList<>();

    // Enumerado para el estado de la orden
    public enum EstadoOrden {
        PENDIENTE, CONFIRMADA, EN_PROCESO, ENVIADA, ENTREGADA, CANCELADA
    }

    // Métodos para agregar items a la orden
    public void agregarItem(Producto producto, int cantidad, BigDecimal precioUnitario) {
        ItemOrden item = new ItemOrden();
        item.setOrden(this);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);
        items.add(item);
    }

    // Método para calcular total de la orden
    public BigDecimal calcularTotal() {
        return items.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}