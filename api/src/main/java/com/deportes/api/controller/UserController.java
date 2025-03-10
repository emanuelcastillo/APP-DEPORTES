package com.deportes.api.controller;

import com.deportes.api.records.ResponseBody;
import com.deportes.api.records.UserRequest;
import com.deportes.api.service.OrdenService;
import com.deportes.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrdenService ordenService;

    @GetMapping("/me")
    public ResponseEntity<ResponseBody<?>> getMe() {
        return ResponseEntity.ok(new ResponseBody<>("User retrieved", userService.getMe()));
    }

    @PatchMapping("/me")
    public ResponseEntity<ResponseBody<?>> updateMe(@RequestBody UserRequest userRequest) {
        userService.updateMe(userRequest);
        return ResponseEntity.ok(new ResponseBody<>("User updated", userService.getMe()));
    }

    @GetMapping("/me/orders")
    public ResponseEntity<ResponseBody<?>> getMyOrders(@RequestParam(name = "page", defaultValue = "0") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(new ResponseBody<>("Orders retrieved", ordenService.findByUsuarioId(userService.getMe().getId(), pageable)));
    }
}
