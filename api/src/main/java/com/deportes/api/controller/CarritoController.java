package com.deportes.api.controller;

import com.deportes.api.entity.Carrito;
import com.deportes.api.entity.ItemCarrito;
import com.deportes.api.entity.Orden;
import com.deportes.api.entity.Producto;
import com.deportes.api.entity.Usuario;
import com.deportes.api.records.ResponseBody;
import com.deportes.api.service.CarritoService;
import com.deportes.api.service.OrdenService;
import com.deportes.api.service.ProductoService;
import com.deportes.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/shopping-cart")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService carritoService;
    private final OrdenService ordenService;
    private final ProductoService productoService;
    private final UserService usuarioService;

    @PostMapping("/add-product/{productId}")
    public ResponseEntity<ResponseBody<?>> addProductToCart(
            @PathVariable Long productId,
            @RequestParam(name = "quantity", required = false, defaultValue = "1") Integer quantity
    ) {
        carritoService.agregarProductoAlCarrito(usuarioService.getMe().getId(), productId, quantity);
        return ResponseEntity.ok(new ResponseBody<>("Product added to cart", null));
    }

    @DeleteMapping("/remove-product/{productId}")
    public ResponseEntity<ResponseBody<?>> removeProductFromCart(
            @PathVariable Long productId
    ) {
        Usuario usuario = usuarioService.getMe();
        carritoService.eliminarProductoDelCarrito(usuario.getId(), productId);
        return ResponseEntity.ok(new ResponseBody<>("Product removed from cart", null));
    }

    @PostMapping("/update-product-quantity/{productId}")
    public ResponseEntity<ResponseBody<?>> updateProductQuantity(
            @PathVariable Long productId,
            @RequestParam(name = "quantity", required = true) Integer quantity
    ) {
        Usuario usuario = usuarioService.getMe();
        carritoService.actualizarCantidadProducto(usuario.getId(), productId, quantity);
        return ResponseEntity.ok(new ResponseBody<>("Product quantity updated", null));
    }

    @PostMapping("/empty-cart")
    public ResponseEntity<ResponseBody<?>> emptyCart() {
        Usuario usuario = usuarioService.getMe();
        carritoService.vaciarCarrito(usuario.getId());
        return ResponseEntity.ok(new ResponseBody<>("Cart emptied", null));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ResponseBody<?>> checkout() {
        Usuario usuario = usuarioService.getMe();
        Orden order = ordenService.crearOrdenDesdeCarrito(usuario.getId(), usuario.getDireccionEnvio());
        return ResponseEntity.ok(new ResponseBody<>("Order created", order));
    }

    @GetMapping("/items")
    public ResponseEntity<ResponseBody<List<ItemCarrito>>> getItems() {
        Usuario usuario = usuarioService.getMe();
        Carrito carrito = carritoService.findCarritoWithItemsByUsuarioId(usuario.getId());
        return ResponseEntity.ok(new ResponseBody<>("Items in cart", carrito.getItems()));
    }

    @PostMapping("/total")
    public ResponseEntity<ResponseBody<BigDecimal>> getTotal() {
        Usuario usuario = usuarioService.getMe();
        return ResponseEntity.ok(new ResponseBody<>("Total amount in cart", carritoService.calcularTotalCarrito(usuario.getId())));
    }

    @PostMapping("/count")
    public ResponseEntity<ResponseBody<Long>> countItems() {
        Usuario usuario = usuarioService.getMe();
        return ResponseEntity.ok(new ResponseBody<>("Number of items in cart", carritoService.contarItemsCarrito(usuario.getId())));
    }
}
