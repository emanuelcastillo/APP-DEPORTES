package com.deportes.api.records;

import java.time.LocalDate;

public record RegisterRequest(String nombre,
                              String apellido,
                              String email,
                              String password,
                              String direccionEnvio,
                              LocalDate fechaNacimiento
                              ) {
}
