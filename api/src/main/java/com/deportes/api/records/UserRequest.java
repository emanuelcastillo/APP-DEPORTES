package com.deportes.api.records;

public record UserRequest(
        String nombre,
        String apellido,
        String direccionEnvio,
        String email
) {
}
