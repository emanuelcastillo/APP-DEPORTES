package com.deportes.api.controller;


import com.deportes.api.records.LoginRequest;
import com.deportes.api.records.RegisterRequest;
import com.deportes.api.records.ResponseBody;
import com.deportes.api.service.AuthService;
import com.deportes.api.service.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/open")
@AllArgsConstructor
public class OpenController {

    private final AuthService authService;
    private final ProductoService productoService;

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<Map<String, String>>> login(@RequestBody LoginRequest loginRequest) {
        var token = authService.login(loginRequest);
        return ResponseEntity.ok(new ResponseBody<>("Login successful", Map.of("token", token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBody<String>> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new ResponseBody<>("Register successful", null));
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseBody<?>> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new ResponseBody<>("Products retrieved", productoService.findAllAvailable(pageable)));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ResponseBody<?>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseBody<>("Product retrieved", productoService.findById(id)));
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<ResponseBody<?>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(new ResponseBody<>("Products retrieved", productoService.findByCategoria(category, page, size)));
    }

}
