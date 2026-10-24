package it.linlo.dashbackend.autenticazione.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "{validation.notBlank}")
        @Size(min = 3, max = 50, message = "{validation.username.size}") String username,
        @NotBlank(message = "{validation.notBlank}")
        @Email(message = "{validation.email.invalid}")
        @Size(max = 150, message = "{validation.email.size}") String email,
        @NotBlank(message = "{validation.notBlank}")
        @Size(min = 8, max = 100, message = "{validation.password.size}") String password) {
}
